package org.galymzhan.financetrackerbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.galymzhan.financetrackerbackend.entity.Account;
import org.galymzhan.financetrackerbackend.entity.Operation;
import org.galymzhan.financetrackerbackend.entity.enums.OperationType;
import org.galymzhan.financetrackerbackend.repository.AccountRepository;
import org.galymzhan.financetrackerbackend.service.AccountBalanceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountBalanceServiceImpl implements AccountBalanceService {

    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public void applyBalanceChange(Operation operation) {
        BigDecimal amount = operation.getAmount();
        OperationType type = operation.getOperationType();

        switch (type) {
            case INCOME -> {
                if (operation.getAccountIn() != null) {
                    updateAccountBalance(operation.getAccountIn(), amount);
                }
            }
            case EXPENSE -> {
                if (operation.getAccountOut() != null) {
                    updateAccountBalance(operation.getAccountOut(), amount.negate());
                }
            }
            case TRANSFER -> {
                if (operation.getAccountOut() != null) {
                    updateAccountBalance(operation.getAccountOut(), amount.negate());
                }
                if (operation.getAccountIn() != null) {
                    updateAccountBalance(operation.getAccountIn(), amount);
                }
            }
        }
    }

    @Override
    @Transactional
    public void revertBalanceChange(Operation operation) {
        BigDecimal amount = operation.getAmount();
        OperationType type = operation.getOperationType();

        switch (type) {
            case INCOME -> {
                if (operation.getAccountIn() != null) {
                    updateAccountBalance(operation.getAccountIn(), amount.negate());
                }
            }
            case EXPENSE -> {
                if (operation.getAccountOut() != null) {
                    updateAccountBalance(operation.getAccountOut(), amount);
                }
            }
            case TRANSFER -> {
                if (operation.getAccountOut() != null) {
                    updateAccountBalance(operation.getAccountOut(), amount);
                }
                if (operation.getAccountIn() != null) {
                    updateAccountBalance(operation.getAccountIn(), amount.negate());
                }
            }
        }
    }

    private void updateAccountBalance(Account account, BigDecimal amount) {
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
    }
} 