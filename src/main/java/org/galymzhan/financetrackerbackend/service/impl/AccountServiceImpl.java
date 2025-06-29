package org.galymzhan.financetrackerbackend.service.impl;

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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final AuthenticationService authenticationService;

    @Override
    public List<AccountResponseDto> getAllAccounts() {
        User user = authenticationService.getCurrentUser();
        return accountRepository.findAllByUser(user).stream()
                .map(accountMapper::toDto)
                .toList();
    }

    @Override
    public AccountResponseDto getAccountById(Long id) throws NotFoundException {
        User user = authenticationService.getCurrentUser();
        Account account = accountRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Account not found"));
        return accountMapper.toDto(account);
    }

    @Override
    public AccountResponseDto create(AccountRequestDto accountRequestDto) throws NotFoundException {
        Account account = accountMapper.toEntity(accountRequestDto);
        account.setUser(authenticationService.getCurrentUser());
        Account savedAccount = accountRepository.save(account);
        return accountMapper.toDto(savedAccount);
    }

    @Override
    public AccountResponseDto update(Long id, AccountRequestDto accountRequestDto) throws NotFoundException {
        User user = authenticationService.getCurrentUser();
        Account account = accountRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Account not found"));
        accountMapper.update(account, accountRequestDto);
        Account updatedAccount = accountRepository.save(account);
        return accountMapper.toDto(accountRepository.save(updatedAccount));
    }

    @Override
    public void delete(Long id) throws NotFoundException {
        User user = authenticationService.getCurrentUser();
        Account account = accountRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Account not found"));
        accountRepository.delete(account);
    }
}
