package org.galymzhan.financetrackerbackend.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.galymzhan.financetrackerbackend.dto.AccountRequestDto;
import org.galymzhan.financetrackerbackend.dto.AccountResponseDto;
import org.galymzhan.financetrackerbackend.entity.Account;
import org.galymzhan.financetrackerbackend.entity.User;
import org.galymzhan.financetrackerbackend.exceptions.NotFoundException;
import org.galymzhan.financetrackerbackend.mapper.AccountMapper;
import org.galymzhan.financetrackerbackend.repository.AccountRepository;
import org.galymzhan.financetrackerbackend.service.AccountService;
import org.galymzhan.financetrackerbackend.service.AuthenticationService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final AuthenticationService authenticationService;

    @Override
    @Cacheable(value = "user-accounts", keyGenerator = "userAwareKeyGenerator")
    public List<AccountResponseDto> getAll() {
        User user = authenticationService.getCurrentUser();
        return accountRepository.findAllByUser(user).stream()
                .map(accountMapper::toResponseDto)
                .toList();
    }

    @Override
    @Cacheable(value = "user-account-by-id", keyGenerator = "userAwareKeyGenerator")
    public AccountResponseDto getById(Long id) {
        User user = authenticationService.getCurrentUser();
        Account account = accountRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Account not found"));
        return accountMapper.toResponseDto(account);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"user-accounts", "user-account-by-id"}, allEntries = true)
    public AccountResponseDto create(AccountRequestDto accountRequestDto) {
        Account account = accountMapper.toEntity(accountRequestDto);
        account.setUser(authenticationService.getCurrentUser());
        Account savedAccount = accountRepository.save(account);
        return accountMapper.toResponseDto(savedAccount);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"user-accounts", "user-account-by-id"}, allEntries = true)
    public AccountResponseDto update(Long id, AccountRequestDto accountRequestDto) {
        User user = authenticationService.getCurrentUser();
        Account account = accountRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Account not found"));
        accountMapper.updateEntity(account, accountRequestDto);
        Account updatedAccount = accountRepository.save(account);
        return accountMapper.toResponseDto(updatedAccount);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"user-accounts", "user-account-by-id"}, allEntries = true)
    public void delete(Long id) {
        User user = authenticationService.getCurrentUser();
        Account account = accountRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Account not found"));
        accountRepository.delete(account);
    }
}
