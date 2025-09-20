package org.galymzhan.financetrackerbackend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.galymzhan.financetrackerbackend.dto.background.BatchResult;
import org.galymzhan.financetrackerbackend.dto.background.TaggingResult;
import org.galymzhan.financetrackerbackend.entity.Operation;
import org.galymzhan.financetrackerbackend.entity.Tag;
import org.galymzhan.financetrackerbackend.entity.User;
import org.galymzhan.financetrackerbackend.entity.rules.TaggingRule;
import org.galymzhan.financetrackerbackend.repository.OperationRepository;
import org.galymzhan.financetrackerbackend.service.BackgroundTaggingService;
import org.galymzhan.financetrackerbackend.service.RuleEngineService;
import org.galymzhan.financetrackerbackend.service.TaggingRuleService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class BackgroundTaggingServiceImpl implements BackgroundTaggingService {

    private final TaggingRuleService taggingRuleService;
    private final RuleEngineService ruleEngineService;
    private final OperationRepository operationRepository;

    @Value("${app.tagging.batch-size:1000}")
    private int batchSize;

    @Override
    @Async("taggingExecutor")
    public CompletableFuture<TaggingResult> processOperationsInBackground(User user, List<Long> operationIds) {
        try {
            List<TaggingRule> rules = taggingRuleService.getUserRules(user);
            if (rules.isEmpty()) {
                return CompletableFuture.completedFuture(TaggingResult.noRules());
            }

            TaggingResult result = new TaggingResult();

            for (int i = 0; i < operationIds.size(); i += batchSize) {
                int endIndex = Math.min(i + batchSize, operationIds.size());
                List<Long> batch = operationIds.subList(i, endIndex);

                BatchResult batchResult = processBatch(user, batch, rules);
                result.addBatchResult(batchResult);
            }

            return CompletableFuture.completedFuture(result);

        } catch (Exception e) {
            log.error("Background tagging failed for user {}: {}", user.getId(), e.getMessage(), e);
            return CompletableFuture.completedFuture(TaggingResult.failed(e.getMessage()));
        }
    }

    public BatchResult processBatch(User user, List<Long> operationIds, List<TaggingRule> rules) {
        List<Operation> operations = operationRepository.findAllByIdInAndUser(operationIds, user);
        BatchResult result = new BatchResult();
        List<Operation> operationsToSave = new ArrayList<>();

        for (Operation operation : operations) {
            try {
                Set<Tag> newTags = ruleEngineService.evaluateRules(operation, rules);

                if (!newTags.isEmpty()) {
                    operation.getTags().addAll(newTags);
                    operationsToSave.add(operation);
                    result.incrementTagged();
                }

                result.incrementProcessed();

            } catch (Exception e) {
                log.warn("Failed to process operation {}: {}", operation.getId(), e.getMessage());
                result.incrementFailed();
            }
        }

        if (!operationsToSave.isEmpty()) {
            operationRepository.saveAll(operationsToSave);
        }

        return result;
    }

    @Override
    @Async("taggingExecutor")
    public CompletableFuture<Void> reapplyAllRules(User user) {

        try {
            List<TaggingRule> rules = taggingRuleService.getUserRules(user);
            if (rules.isEmpty()) {
                return CompletableFuture.completedFuture(null);
            }

            int page = 0;
            Page<Operation> operationsPage;

            do {
                Pageable pageable = PageRequest.of(page, batchSize);
                operationsPage = operationRepository.findAllByUser(user, pageable);

                for (Operation operation : operationsPage.getContent()) {
                    Set<Tag> suggestedTags = ruleEngineService.evaluateRules(operation, rules);

                    operation.getTags().clear();
                    operation.getTags().addAll(suggestedTags);
                    operationRepository.save(operation);
                }

                page++;

            } while (operationsPage.hasNext());

            return CompletableFuture.completedFuture(null);

        } catch (Exception e) {
            log.error("Full rule reapplication failed for user {}: {}", user.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to reapply rules", e);
        }
    }
}