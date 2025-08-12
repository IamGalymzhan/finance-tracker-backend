package org.galymzhan.financetrackerbackend.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.galymzhan.financetrackerbackend.dto.request.TaggingRuleRequestDto;
import org.galymzhan.financetrackerbackend.dto.response.TaggingRuleResponseDto;
import org.galymzhan.financetrackerbackend.entity.Tag;
import org.galymzhan.financetrackerbackend.entity.User;
import org.galymzhan.financetrackerbackend.entity.rules.TaggingCondition;
import org.galymzhan.financetrackerbackend.entity.rules.TaggingRule;
import org.galymzhan.financetrackerbackend.exceptions.NotFoundException;
import org.galymzhan.financetrackerbackend.mapper.TaggingRuleMapper;
import org.galymzhan.financetrackerbackend.repository.TagRepository;
import org.galymzhan.financetrackerbackend.repository.TaggingConditionRepository;
import org.galymzhan.financetrackerbackend.repository.TaggingRuleRepository;
import org.galymzhan.financetrackerbackend.service.AuthenticationService;
import org.galymzhan.financetrackerbackend.service.TaggingRuleService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaggingRuleServiceImpl implements TaggingRuleService {

    private final TaggingRuleRepository taggingRuleRepository;
    private final TaggingConditionRepository taggingConditionRepository;
    private final TagRepository tagRepository;
    private final TaggingRuleMapper taggingRuleMapper;
    private final AuthenticationService authenticationService;

    @Override
    @Cacheable(value = "user-tagging-rules", keyGenerator = "userAwareKeyGenerator")
    public List<TaggingRuleResponseDto> getAll() {
        User user = authenticationService.getCurrentUser();
        return taggingRuleRepository.findAllByUser(user).stream()
                .map(taggingRuleMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "user-tagging-rule-by-id", keyGenerator = "userAwareKeyGenerator")
    public TaggingRuleResponseDto getById(Long id) {
        User user = authenticationService.getCurrentUser();
        TaggingRule rule = taggingRuleRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Tagging rule not found"));
        return taggingRuleMapper.toResponseDto(rule);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"user-tagging-rules", "user-tagging-rule-by-id", "user-active-tagging-rules"}, allEntries = true)
    public TaggingRuleResponseDto create(TaggingRuleRequestDto requestDto) {
        User user = authenticationService.getCurrentUser();

        if (requestDto.getConditions().isEmpty()) {
            throw new IllegalArgumentException("Conditions list cannot be empty");
        }

        TaggingRule rule = taggingRuleMapper.toEntity(requestDto);
        rule.setUser(user);
        rule.setActive(requestDto.getActive() != null ? requestDto.getActive() : true);

        Set<Tag> tags = new HashSet<>(tagRepository.findAllByIdInAndUser(requestDto.getTagIds(), user));
        rule.setTagsToApply(tags);

        rule.setConditions(new ArrayList<>());

        TaggingRule savedRule = taggingRuleRepository.save(rule);

        List<TaggingCondition> conditions = requestDto.getConditions().stream()
                .map(conditionDto -> {
                    TaggingCondition condition = taggingRuleMapper.toConditionEntity(conditionDto);
                    condition.setRule(savedRule);
                    return condition;
                })
                .collect(Collectors.toList());

        savedRule.getConditions().addAll(conditions);

        taggingConditionRepository.saveAll(conditions);

        return taggingRuleMapper.toResponseDto(savedRule);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"user-tagging-rules", "user-tagging-rule-by-id", "user-active-tagging-rules"}, allEntries = true)
    public TaggingRuleResponseDto update(Long id, TaggingRuleRequestDto requestDto) {
        User user = authenticationService.getCurrentUser();
        TaggingRule rule = taggingRuleRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Tagging rule not found"));

        taggingRuleMapper.updateEntity(rule, requestDto);
        if (requestDto.getActive() != null) {
            rule.setActive(requestDto.getActive());
        }

        if (requestDto.getTagIds() != null) {
            Set<Tag> tags = new HashSet<>(tagRepository.findAllByIdInAndUser(requestDto.getTagIds(), user));
            rule.setTagsToApply(tags);
        }

        if (requestDto.getConditions() != null) {
            if (requestDto.getConditions().isEmpty()) {
                throw new IllegalArgumentException("Conditions list cannot be empty.");
            }

            if (rule.getConditions() == null) {
                rule.setConditions(new ArrayList<>());
            }

            rule.getConditions().clear();

            List<TaggingCondition> newConditions = requestDto.getConditions().stream()
                    .map(conditionDto -> {
                        TaggingCondition condition = taggingRuleMapper.toConditionEntity(conditionDto);
                        condition.setRule(rule);
                        return condition;
                    })
                    .toList();

            rule.getConditions().addAll(newConditions);
        }

        TaggingRule updatedRule = taggingRuleRepository.save(rule);
        return taggingRuleMapper.toResponseDto(updatedRule);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"user-tagging-rules", "user-tagging-rule-by-id", "user-active-tagging-rules"}, allEntries = true)
    public void delete(Long id) {
        User user = authenticationService.getCurrentUser();
        TaggingRule rule = taggingRuleRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Tagging rule not found"));
        taggingRuleRepository.delete(rule);
    }

    @Override
    @Cacheable(value = "user-active-tagging-rules", keyGenerator = "userAwareKeyGenerator")
    public List<TaggingRule> getUserRules() {
        User user = authenticationService.getCurrentUser();
        return taggingRuleRepository.findAllByUserAndActiveTrue(user);
    }
}