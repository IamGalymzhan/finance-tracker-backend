package org.galymzhan.financetrackerbackend.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.galymzhan.financetrackerbackend.dto.OperationFilterDto;
import org.galymzhan.financetrackerbackend.dto.OperationRequestDto;
import org.galymzhan.financetrackerbackend.dto.OperationResponseDto;
import org.galymzhan.financetrackerbackend.entity.*;
import org.galymzhan.financetrackerbackend.exceptions.NotFoundException;
import org.galymzhan.financetrackerbackend.mapper.OperationMapper;
import org.galymzhan.financetrackerbackend.repository.AccountRepository;
import org.galymzhan.financetrackerbackend.repository.CategoryRepository;
import org.galymzhan.financetrackerbackend.repository.OperationRepository;
import org.galymzhan.financetrackerbackend.repository.TagRepository;
import org.galymzhan.financetrackerbackend.service.AuthenticationService;
import org.galymzhan.financetrackerbackend.service.OperationService;
import org.galymzhan.financetrackerbackend.specification.OperationSpecification;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OperationServiceImpl implements OperationService {

    private final OperationMapper operationMapper;
    private final AuthenticationService authenticationService;

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
            @CacheEvict(value = "user-reports", allEntries = true)
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
        return operationMapper.toResponseDto(savedOperation);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "user-operations", allEntries = true),
            @CacheEvict(value = "user-operation-by-id", allEntries = true),
            @CacheEvict(value = "user-reports", allEntries = true)
    })
    public OperationResponseDto update(Long id, OperationRequestDto operationRequestDto) {
        User user = authenticationService.getCurrentUser();
        Operation operation = operationRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Operation Not Found"));

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
        return operationMapper.toResponseDto(updatedOperation);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "user-operations", allEntries = true),
            @CacheEvict(value = "user-operation-by-id", allEntries = true),
            @CacheEvict(value = "user-reports", allEntries = true)
    })
    public void delete(Long id) {
        User user = authenticationService.getCurrentUser();
        Operation operation = operationRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Operation Not Found"));
        operationRepository.delete(operation);
    }
}
