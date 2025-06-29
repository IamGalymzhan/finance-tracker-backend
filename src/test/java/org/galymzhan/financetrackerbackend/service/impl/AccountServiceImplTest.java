package org.galymzhan.financetrackerbackend.service.impl;

import org.galymzhan.financetrackerbackend.dto.AccountRequestDto;
import org.galymzhan.financetrackerbackend.dto.AccountResponseDto;
import org.galymzhan.financetrackerbackend.entity.Account;
import org.galymzhan.financetrackerbackend.entity.AccountType;
import org.galymzhan.financetrackerbackend.entity.Role;
import org.galymzhan.financetrackerbackend.entity.User;
import org.galymzhan.financetrackerbackend.exceptions.NotFoundException;
import org.galymzhan.financetrackerbackend.mapper.AccountMapper;
import org.galymzhan.financetrackerbackend.repository.AccountRepository;
import org.galymzhan.financetrackerbackend.service.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AccountServiceImpl accountService;

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

    private AccountRequestDto createTestAccountRequestDto() {
        return AccountRequestDto.builder()
                .name("Test Account")
                .accountType("DEBIT")
                .balance(new BigDecimal("1000.00"))
                .color("#FF0000")
                .icon("wallet")
                .build();
    }

    private AccountResponseDto createTestAccountResponseDto() {
        return AccountResponseDto.builder()
                .id(1L)
                .name("Test Account")
                .accountType("DEBIT")
                .balance(new BigDecimal("1000.00"))
                .color("#FF0000")
                .icon("wallet")
                .build();
    }

    @Test
    public void getAllAccounts_ShouldReturnAccountList_WhenUserHasAccounts() {
        User user = createTestUser();
        List<Account> accounts = List.of(createTestAccount());
        AccountResponseDto responseDto = createTestAccountResponseDto();

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(accountRepository.findAllByUser(user)).thenReturn(accounts);
        when(accountMapper.toResponseDto(any(Account.class))).thenReturn(responseDto);

        List<AccountResponseDto> result = accountService.getAllAccounts();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(responseDto);
        verify(authenticationService).getCurrentUser();
        verify(accountRepository).findAllByUser(user);
        verify(accountMapper).toResponseDto(any(Account.class));
    }

    @Test
    public void getAccountById_ShouldReturnAccount_WhenAccountExists() {
        Long accountId = 1L;
        User user = createTestUser();
        Account account = createTestAccount();
        AccountResponseDto responseDto = createTestAccountResponseDto();

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(accountRepository.findByIdAndUser(accountId, user)).thenReturn(Optional.of(account));
        when(accountMapper.toResponseDto(account)).thenReturn(responseDto);

        AccountResponseDto result = accountService.getAccountById(accountId);

        assertThat(result).isEqualTo(responseDto);
        verify(authenticationService).getCurrentUser();
        verify(accountRepository).findByIdAndUser(accountId, user);
        verify(accountMapper).toResponseDto(account);
    }

    @Test
    public void getAccountById_ShouldThrowNotFoundException_WhenAccountNotFound() {
        Long accountId = 1L;
        User user = createTestUser();

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(accountRepository.findByIdAndUser(accountId, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.getAccountById(accountId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Account not found");

        verify(authenticationService).getCurrentUser();
        verify(accountRepository).findByIdAndUser(accountId, user);
        verifyNoInteractions(accountMapper);
    }

    @Test
    public void create_ShouldCreateAndReturnAccount_WhenValidInput() {
        User user = createTestUser();
        AccountRequestDto requestDto = createTestAccountRequestDto();
        Account account = createTestAccount();
        Account savedAccount = createTestAccount();
        AccountResponseDto responseDto = createTestAccountResponseDto();

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(accountMapper.toEntity(requestDto)).thenReturn(account);
        when(accountRepository.save(account)).thenReturn(savedAccount);
        when(accountMapper.toResponseDto(savedAccount)).thenReturn(responseDto);

        AccountResponseDto result = accountService.create(requestDto);

        assertThat(result).isEqualTo(responseDto);
        verify(authenticationService).getCurrentUser();
        verify(accountMapper).toEntity(requestDto);
        verify(accountRepository).save(account);
        verify(accountMapper).toResponseDto(savedAccount);
    }

    @Test
    public void update_ShouldUpdateAndReturnAccount_WhenAccountExists() {
        Long accountId = 1L;
        User user = createTestUser();
        AccountRequestDto requestDto = createTestAccountRequestDto();
        Account account = createTestAccount();
        Account updatedAccount = createTestAccount();
        AccountResponseDto responseDto = createTestAccountResponseDto();

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(accountRepository.findByIdAndUser(accountId, user)).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(updatedAccount);
        when(accountMapper.toResponseDto(updatedAccount)).thenReturn(responseDto);

        AccountResponseDto result = accountService.update(accountId, requestDto);

        assertThat(result).isEqualTo(responseDto);
        verify(authenticationService).getCurrentUser();
        verify(accountRepository).findByIdAndUser(accountId, user);
        verify(accountMapper).updateEntity(account, requestDto);
        verify(accountRepository).save(account);
        verify(accountMapper).toResponseDto(updatedAccount);
    }

    @Test
    public void update_ShouldThrowNotFoundException_WhenAccountNotFound() {
        Long accountId = 1L;
        User user = createTestUser();
        AccountRequestDto requestDto = createTestAccountRequestDto();

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(accountRepository.findByIdAndUser(accountId, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.update(accountId, requestDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Account not found");

        verify(authenticationService).getCurrentUser();
        verify(accountRepository).findByIdAndUser(accountId, user);
        verifyNoMoreInteractions(accountMapper, accountRepository);
    }

    @Test
    public void delete_ShouldDeleteAccount_WhenAccountExists() {
        Long accountId = 1L;
        User user = createTestUser();
        Account account = createTestAccount();

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(accountRepository.findByIdAndUser(accountId, user)).thenReturn(Optional.of(account));

        accountService.delete(accountId);

        verify(authenticationService).getCurrentUser();
        verify(accountRepository).findByIdAndUser(accountId, user);
        verify(accountRepository).delete(account);
    }

    @Test
    public void delete_ShouldThrowNotFoundException_WhenAccountNotFound() {
        Long accountId = 1L;
        User user = createTestUser();

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(accountRepository.findByIdAndUser(accountId, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.delete(accountId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Account not found");

        verify(authenticationService).getCurrentUser();
        verify(accountRepository).findByIdAndUser(accountId, user);
        verify(accountRepository, never()).delete(any());
    }
}
