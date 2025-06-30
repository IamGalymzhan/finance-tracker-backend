package org.galymzhan.financetrackerbackend.service.impl;

import lombok.RequiredArgsConstructor;
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
    public List<OperationResponseDto> getAll() {
        User user = authenticationService.getCurrentUser();
        return operationRepository.findAllByUser(user)
                .stream()
                .map(operationMapper::toResponseDto)
                .toList();
    }

    @Override
    public OperationResponseDto getById(Long id) {
        User user = authenticationService.getCurrentUser();
        Operation operation = operationRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Operation Not Found"));
        return operationMapper.toResponseDto(operation);
    }

    @Override
    public OperationResponseDto create(OperationRequestDto operationRequestDto) {
        Operation operation = operationMapper.toEntity(operationRequestDto);
        User user = authenticationService.getCurrentUser();

        operation.setUser(user);

        Category category = categoryRepository.findByIdAndUser(operationRequestDto.getCategoryId(), user)
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + operationRequestDto.getCategoryId()));
        operation.setCategory(category);

        if (operationRequestDto.getAccountInId() != null) {
            Account accountIn = accountRepository.findById(operationRequestDto.getAccountInId())
                    .orElseThrow(() -> new NotFoundException("Account not found with id: " + operationRequestDto.getAccountInId()));
            operation.setAccountIn(accountIn);
        }

        if (operationRequestDto.getAccountOutId() != null) {
            Account accountOut = accountRepository.findById(operationRequestDto.getAccountOutId())
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
            Account accountIn = accountRepository.findById(operationRequestDto.getAccountInId())
                    .orElseThrow(() -> new NotFoundException("Account not found with id: " + operationRequestDto.getAccountInId()));
            operation.setAccountIn(accountIn);
        }

        if (operationRequestDto.getAccountOutId() != null) {
            Account accountOut = accountRepository.findById(operationRequestDto.getAccountOutId())
                    .orElseThrow(() -> new NotFoundException("Account not found with id: " + operationRequestDto.getAccountOutId()));
            operation.setAccountOut(accountOut);
        }

        if (operationRequestDto.getTagIds() != null && !operationRequestDto.getTagIds().isEmpty()) {
            Set<Tag> tags = new HashSet<>(tagRepository.findAllByIdInAndUser(operationRequestDto.getTagIds(), user));
            operation.setTags(tags);
        }

        operationMapper.updateEntity(operation, operationRequestDto);
        Operation updatedOperation = operationRepository.save(operation);
        return operationMapper.toResponseDto(updatedOperation);
    }

    @Override
    public void delete(Long id) {
        User user = authenticationService.getCurrentUser();
        Operation operation = operationRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Operation Not Found"));
        operationRepository.delete(operation);
    }
}
