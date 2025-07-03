package org.galymzhan.financetrackerbackend.service.impl;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OperationServiceImplTest {

    @Mock
    private OperationRepository operationRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private OperationMapper operationMapper;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private OperationServiceImpl operationService;

    private User createTestUser() {
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .role(Role.ROLE_USER)
                .build();
        user.setId(1L);
        return user;
    }

    private Category createTestCategory() {
        Category category = Category.builder()
                .name("Test Category")
                .direction(Direction.EXPENSE)
                .color("#FF0000")
                .user(createTestUser())
                .build();
        category.setId(1L);
        return category;
    }

    private Account createTestAccount() {
        Account account = Account.builder()
                .name("Test Account")
                .accountType(AccountType.DEBIT)
                .balance(new BigDecimal("1000.00"))
                .color("#FF0000")
                .icon("wallet")
                .user(createTestUser())
                .build();
        account.setId(1L);
        return account;
    }

    private Tag createTestTag() {
        Tag tag = Tag.builder()
                .name("Test Tag")
                .color("#FF0000")
                .user(createTestUser())
                .build();
        tag.setId(1L);
        return tag;
    }

    private Operation createTestOperation() {
        Operation operation = Operation.builder()
                .name("Test Operation")
                .operationType(OperationType.EXPENSE)
                .amount(new BigDecimal("100.00"))
                .note("Test note")
                .user(createTestUser())
                .category(createTestCategory())
                .accountOut(createTestAccount())
                .tags(new HashSet<>(Set.of(createTestTag())))
                .build();
        operation.setId(1L);
        return operation;
    }

    private OperationRequestDto createTestOperationRequestDto() {
        return OperationRequestDto.builder()
                .name("Test Operation")
                .operationType(OperationType.EXPENSE)
                .categoryId(1L)
                .amount(new BigDecimal("100.00"))
                .accountOutId(1L)
                .note("Test note")
                .tagIds(Set.of(1L))
                .build();
    }

    private OperationResponseDto createTestOperationResponseDto() {
        return OperationResponseDto.builder()
                .id(1L)
                .name("Test Operation")
                .operationType(OperationType.EXPENSE)
                .amount(new BigDecimal("100.00"))
                .note("Test note")
                .category(OperationResponseDto.CategorySummaryDto.builder()
                        .id(1L)
                        .name("Test Category")
                        .build())
                .accountOut(OperationResponseDto.AccountSummaryDto.builder()
                        .id(1L)
                        .name("Test Account")
                        .accountType(AccountType.DEBIT)
                        .build())
                .tags(Set.of(OperationResponseDto.TagSummaryDto.builder()
                        .id(1L)
                        .name("Test Tag")
                        .build()))
                .build();
    }

    @Test
    public void getAll_ShouldReturnOperationList_WhenUserHas() {
        User user = createTestUser();
        List<Operation> operations = List.of(createTestOperation());
        OperationResponseDto responseDto = createTestOperationResponseDto();

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(operationRepository.findAllByUser(user)).thenReturn(operations);
        when(operationMapper.toResponseDto(any(Operation.class))).thenReturn(responseDto);

        List<OperationResponseDto> result = operationService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(responseDto);
        verify(authenticationService).getCurrentUser();
        verify(operationRepository).findAllByUser(user);
        verify(operationMapper).toResponseDto(any(Operation.class));
    }

    @Test
    public void getById_ShouldReturnOperation_WhenExists() {
        Long operationId = 1L;
        User user = createTestUser();
        Operation operation = createTestOperation();
        OperationResponseDto responseDto = createTestOperationResponseDto();

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(operationRepository.findByIdAndUser(operationId, user)).thenReturn(Optional.of(operation));
        when(operationMapper.toResponseDto(operation)).thenReturn(responseDto);

        OperationResponseDto result = operationService.getById(operationId);

        assertThat(result).isEqualTo(responseDto);
        verify(authenticationService).getCurrentUser();
        verify(operationRepository).findByIdAndUser(operationId, user);
        verify(operationMapper).toResponseDto(operation);
    }

    @Test
    public void getById_ShouldThrowNotFoundException_WhenNotFound() {
        Long operationId = 1L;
        User user = createTestUser();

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(operationRepository.findByIdAndUser(operationId, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> operationService.getById(operationId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Operation Not Found");

        verify(authenticationService).getCurrentUser();
        verify(operationRepository).findByIdAndUser(operationId, user);
        verifyNoInteractions(operationMapper);
    }

    @Test
    public void create_ShouldCreateAndReturnOperation_WhenValidInput() {
        User user = createTestUser();
        OperationRequestDto requestDto = createTestOperationRequestDto();
        Operation operation = createTestOperation();
        Operation savedOperation = createTestOperation();
        OperationResponseDto responseDto = createTestOperationResponseDto();
        Category category = createTestCategory();
        Account account = createTestAccount();
        List<Tag> tags = List.of(createTestTag());

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(operationMapper.toEntity(requestDto)).thenReturn(operation);
        when(categoryRepository.findByIdAndUser(requestDto.getCategoryId(), user)).thenReturn(Optional.of(category));
        when(accountRepository.findById(requestDto.getAccountOutId())).thenReturn(Optional.of(account));
        when(tagRepository.findAllByIdInAndUser(requestDto.getTagIds(), user)).thenReturn(tags);
        when(operationRepository.save(operation)).thenReturn(savedOperation);
        when(operationMapper.toResponseDto(savedOperation)).thenReturn(responseDto);

        OperationResponseDto result = operationService.create(requestDto);

        assertThat(result).isEqualTo(responseDto);
        verify(authenticationService).getCurrentUser();
        verify(operationMapper).toEntity(requestDto);
        verify(categoryRepository).findByIdAndUser(requestDto.getCategoryId(), user);
        verify(accountRepository).findById(requestDto.getAccountOutId());
        verify(tagRepository).findAllByIdInAndUser(requestDto.getTagIds(), user);
        verify(operationRepository).save(operation);
        verify(operationMapper).toResponseDto(savedOperation);
    }

    @Test
    public void create_ShouldThrowNotFoundException_WhenCategoryNotFound() {
        User user = createTestUser();
        OperationRequestDto requestDto = createTestOperationRequestDto();
        Operation operation = createTestOperation();

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(operationMapper.toEntity(requestDto)).thenReturn(operation);
        when(categoryRepository.findByIdAndUser(requestDto.getCategoryId(), user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> operationService.create(requestDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Category not found with id: " + requestDto.getCategoryId());

        verify(authenticationService).getCurrentUser();
        verify(operationMapper).toEntity(requestDto);
        verify(categoryRepository).findByIdAndUser(requestDto.getCategoryId(), user);
        verifyNoInteractions(operationRepository);
    }

    @Test
    public void update_ShouldUpdateAndReturnOperation_WhenOperationExists() {
        Long operationId = 1L;
        User user = createTestUser();
        OperationRequestDto requestDto = createTestOperationRequestDto();
        Operation operation = createTestOperation();
        Operation updatedOperation = createTestOperation();
        OperationResponseDto responseDto = createTestOperationResponseDto();
        Category category = createTestCategory();
        Account account = createTestAccount();
        List<Tag> tags = List.of(createTestTag());

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(operationRepository.findByIdAndUser(operationId, user)).thenReturn(Optional.of(operation));
        when(categoryRepository.findByIdAndUser(requestDto.getCategoryId(), user)).thenReturn(Optional.of(category));
        when(accountRepository.findById(requestDto.getAccountOutId())).thenReturn(Optional.of(account));
        when(tagRepository.findAllByIdInAndUser(requestDto.getTagIds(), user)).thenReturn(tags);
        when(operationRepository.save(operation)).thenReturn(updatedOperation);
        when(operationMapper.toResponseDto(updatedOperation)).thenReturn(responseDto);

        OperationResponseDto result = operationService.update(operationId, requestDto);

        assertThat(result).isEqualTo(responseDto);
        verify(authenticationService).getCurrentUser();
        verify(operationRepository).findByIdAndUser(operationId, user);
        verify(categoryRepository).findByIdAndUser(requestDto.getCategoryId(), user);
        verify(accountRepository).findById(requestDto.getAccountOutId());
        verify(tagRepository).findAllByIdInAndUser(requestDto.getTagIds(), user);
        verify(operationMapper).updateEntity(operation, requestDto);
        verify(operationRepository).save(operation);
        verify(operationMapper).toResponseDto(updatedOperation);
    }

    @Test
    public void update_ShouldThrowNotFoundException_WhenOperationNotFound() {
        Long operationId = 1L;
        User user = createTestUser();
        OperationRequestDto requestDto = createTestOperationRequestDto();

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(operationRepository.findByIdAndUser(operationId, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> operationService.update(operationId, requestDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Operation Not Found");

        verify(authenticationService).getCurrentUser();
        verify(operationRepository).findByIdAndUser(operationId, user);
        verifyNoMoreInteractions(categoryRepository, accountRepository, tagRepository, operationMapper);
    }

    @Test
    public void delete_ShouldDeleteOperation_WhenOperationExists() {
        Long operationId = 1L;
        User user = createTestUser();
        Operation operation = createTestOperation();

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(operationRepository.findByIdAndUser(operationId, user)).thenReturn(Optional.of(operation));

        operationService.delete(operationId);

        verify(authenticationService).getCurrentUser();
        verify(operationRepository).findByIdAndUser(operationId, user);
        verify(operationRepository).delete(operation);
    }

    @Test
    public void delete_ShouldThrowNotFoundException_WhenOperationNotFound() {
        Long operationId = 1L;
        User user = createTestUser();

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(operationRepository.findByIdAndUser(operationId, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> operationService.delete(operationId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Operation Not Found");

        verify(authenticationService).getCurrentUser();
        verify(operationRepository).findByIdAndUser(operationId, user);
        verify(operationRepository, never()).delete(any(Operation.class));
    }
}