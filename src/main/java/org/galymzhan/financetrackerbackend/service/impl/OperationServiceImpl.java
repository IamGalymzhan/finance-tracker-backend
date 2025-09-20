package org.galymzhan.financetrackerbackend.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.galymzhan.financetrackerbackend.dto.filter.OperationFilterDto;
import org.galymzhan.financetrackerbackend.dto.request.OperationRequestDto;
import org.galymzhan.financetrackerbackend.dto.response.OperationResponseDto;
import org.galymzhan.financetrackerbackend.entity.*;
import org.galymzhan.financetrackerbackend.entity.rules.TaggingRule;
import org.galymzhan.financetrackerbackend.exceptions.NotFoundException;
import org.galymzhan.financetrackerbackend.mapper.OperationMapper;
import org.galymzhan.financetrackerbackend.repository.AccountRepository;
import org.galymzhan.financetrackerbackend.repository.CategoryRepository;
import org.galymzhan.financetrackerbackend.repository.OperationRepository;
import org.galymzhan.financetrackerbackend.repository.TagRepository;
import org.galymzhan.financetrackerbackend.service.*;
import org.galymzhan.financetrackerbackend.specification.OperationSpecification;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class OperationServiceImpl implements OperationService {

    private final OperationMapper operationMapper;

    private final AuthenticationService authenticationService;
    private final AccountBalanceService accountBalanceService;
    private final RuleEngineService ruleEngineService;
    private final TaggingRuleService taggingRuleService;


    private final OperationRepository operationRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    @Override
    @Cacheable(value = "user-operations", keyGenerator = "userAwareKeyGenerator")
    public List<OperationResponseDto> getAll() {
        User user = authenticationService.getCurrentUser();
        return operationRepository.findAllByUser(user)
                .stream()
                .map(operationMapper::toResponseDto)
                .toList();
    }

    @Override
    public Page<OperationResponseDto> getAllFiltered(OperationFilterDto filters, Pageable pageable) {
        User currentUser = authenticationService.getCurrentUser();

        Specification<Operation> spec = OperationSpecification.withFilters(filters, currentUser);
        Page<Operation> operations = operationRepository.findAll(spec, pageable);

        return operations.map(operationMapper::toResponseDto);
    }

    @Override
    @Cacheable(value = "user-operation-by-id", keyGenerator = "userAwareKeyGenerator")
    public OperationResponseDto getById(Long id) {
        User user = authenticationService.getCurrentUser();
        Operation operation = operationRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Operation Not Found"));
        return operationMapper.toResponseDto(operation);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "user-operations", allEntries = true),
            @CacheEvict(value = "user-operation-by-id", allEntries = true),
            @CacheEvict(value = "user-reports", allEntries = true),
            @CacheEvict(value = "user-accounts", allEntries = true),
            @CacheEvict(value = "user-account-by-id", allEntries = true)
    })
    public OperationResponseDto create(OperationRequestDto operationRequestDto) {
        Operation operation = operationMapper.toEntity(operationRequestDto);
        User user = authenticationService.getCurrentUser();

        operation.setUser(user);

        Category category = categoryRepository.findByIdAndUser(operationRequestDto.getCategoryId(), user)
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + operationRequestDto.getCategoryId()));
        operation.setCategory(category);

        if (operationRequestDto.getAccountInId() != null) {
            Account accountIn = accountRepository.findByIdAndUser(operationRequestDto.getAccountInId(), user)
                    .orElseThrow(() -> new NotFoundException("Account not found with id: " + operationRequestDto.getAccountInId()));
            operation.setAccountIn(accountIn);
        }

        if (operationRequestDto.getAccountOutId() != null) {
            Account accountOut = accountRepository.findByIdAndUser(operationRequestDto.getAccountOutId(), user)
                    .orElseThrow(() -> new NotFoundException("Account not found with id: " + operationRequestDto.getAccountOutId()));
            operation.setAccountOut(accountOut);
        }

        if (operationRequestDto.getTagIds() != null && !operationRequestDto.getTagIds().isEmpty()) {
            Set<Tag> tags = new HashSet<>(tagRepository.findAllByIdInAndUser(operationRequestDto.getTagIds(), user));
            operation.setTags(tags);
        }

        Operation savedOperation = operationRepository.save(operation);

        applyTaggingRules(savedOperation);

        accountBalanceService.applyBalanceChange(savedOperation);

        return operationMapper.toResponseDto(savedOperation);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "user-operations", allEntries = true),
            @CacheEvict(value = "user-operation-by-id", allEntries = true),
            @CacheEvict(value = "user-reports", allEntries = true),
            @CacheEvict(value = "user-accounts", allEntries = true),
            @CacheEvict(value = "user-account-by-id", allEntries = true)
    })
    public void createBatch(List<OperationRequestDto> operationRequestDtos) {
        User user = authenticationService.getCurrentUser();

        Set<Long> categoryIds = operationRequestDtos.stream()
                .map(OperationRequestDto::getCategoryId)
                .collect(Collectors.toSet());

        Set<Long> accountInIds = operationRequestDtos.stream()
                .map(OperationRequestDto::getAccountInId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<Long> accountOutIds = operationRequestDtos.stream()
                .map(OperationRequestDto::getAccountOutId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<Long> tagIds = operationRequestDtos.stream()
                .flatMap(dto -> Optional.ofNullable(dto.getTagIds()).orElse(Set.of()).stream())
                .collect(Collectors.toSet());

        Map<Long, Category> categories = categoryRepository.findAllByIdInAndUser(categoryIds, user)
                .stream().collect(Collectors.toMap(Category::getId, c -> c));

        Map<Long, Account> accounts = accountRepository.findAllByIdInAndUser(
                Stream.concat(accountInIds.stream(), accountOutIds.stream()).toList(),
                user
        ).stream().collect(Collectors.toMap(Account::getId, a -> a));

        Map<Long, Tag> tags = tagRepository.findAllByIdInAndUser(tagIds, user)
                .stream().collect(Collectors.toMap(Tag::getId, t -> t));

        List<Operation> operations = new ArrayList<>();
        for (OperationRequestDto dto : operationRequestDtos) {
            Operation operation = operationMapper.toEntity(dto);
            operation.setUser(user);

            operation.setCategory(categories.get(dto.getCategoryId()));

            if (dto.getAccountInId() != null) {
                operation.setAccountIn(accounts.get(dto.getAccountInId()));
            }

            if (dto.getAccountOutId() != null) {
                operation.setAccountOut(accounts.get(dto.getAccountOutId()));
            }

            if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
                Set<Tag> opTags = dto.getTagIds().stream()
                        .map(tags::get)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());
                operation.setTags(opTags);
            }

            operations.add(operation);
        }

        List<Operation> savedOperations = operationRepository.saveAll(operations);
        for (Operation op : savedOperations) {
            accountBalanceService.applyBalanceChange(op);
        }
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "user-operations", allEntries = true),
            @CacheEvict(value = "user-operation-by-id", allEntries = true),
            @CacheEvict(value = "user-reports", allEntries = true),
            @CacheEvict(value = "user-accounts", allEntries = true),
            @CacheEvict(value = "user-account-by-id", allEntries = true)
    })
    public OperationResponseDto update(Long id, OperationRequestDto operationRequestDto) {
        User user = authenticationService.getCurrentUser();
        Operation operation = operationRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Operation Not Found"));

        accountBalanceService.revertBalanceChange(operation);


        if (operationRequestDto.getCategoryId() != null) {
            Category category = categoryRepository.findByIdAndUser(operationRequestDto.getCategoryId(), user)
                    .orElseThrow(() -> new NotFoundException("Category not found with id: " + operationRequestDto.getCategoryId()));
            operation.setCategory(category);
        }

        if (operationRequestDto.getAccountInId() != null) {
            Account accountIn = accountRepository.findByIdAndUser(operationRequestDto.getAccountInId(), user)
                    .orElseThrow(() -> new NotFoundException("Account not found with id: " + operationRequestDto.getAccountInId()));
            operation.setAccountIn(accountIn);
        }

        if (operationRequestDto.getAccountOutId() != null) {
            Account accountOut = accountRepository.findByIdAndUser(operationRequestDto.getAccountOutId(), user)
                    .orElseThrow(() -> new NotFoundException("Account not found with id: " + operationRequestDto.getAccountOutId()));
            operation.setAccountOut(accountOut);
        }

        Set<Tag> tags;
        if (operationRequestDto.getTagIds() != null && !operationRequestDto.getTagIds().isEmpty()) {
            tags = new HashSet<>(tagRepository.findAllByIdInAndUser(operationRequestDto.getTagIds(), user));
        } else {
            tags = new HashSet<>();
        }
        operation.setTags(tags);

        operationMapper.updateEntity(operation, operationRequestDto);
        Operation updatedOperation = operationRepository.save(operation);

        applyTaggingRules(updatedOperation);

        accountBalanceService.applyBalanceChange(updatedOperation);

        return operationMapper.toResponseDto(updatedOperation);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "user-operations", allEntries = true),
            @CacheEvict(value = "user-operation-by-id", allEntries = true),
            @CacheEvict(value = "user-reports", allEntries = true),
            @CacheEvict(value = "user-accounts", allEntries = true),
            @CacheEvict(value = "user-account-by-id", allEntries = true)
    })
    public void delete(Long id) {
        User user = authenticationService.getCurrentUser();
        Operation operation = operationRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Operation Not Found"));

        accountBalanceService.revertBalanceChange(operation);

        operationRepository.delete(operation);
    }

    private void applyTaggingRules(Operation operation) {
        try {
            User user = authenticationService.getCurrentUser();
            List<TaggingRule> rules = taggingRuleService.getUserRules(user);
            if (!rules.isEmpty()) {
                Set<Tag> autoTags = ruleEngineService.evaluateRules(operation, rules);

                operation.getTags().addAll(autoTags);

                if (!autoTags.isEmpty()) {
                    operationRepository.save(operation);
                }
            }
        } catch (Exception e) {
            log.warn("Failed to apply tagging rules to operation {}: {}", operation.getId(), e.getMessage());
        }
    }
}
