package org.galymzhan.financetrackerbackend.service;

import org.galymzhan.financetrackerbackend.dto.AccountRequestDto;
import org.galymzhan.financetrackerbackend.dto.AccountResponseDto;
import org.galymzhan.financetrackerbackend.exceptions.NotFoundException;

import java.util.List;

public interface AccountService {

    List<AccountResponseDto> getAllAccounts();

    AccountResponseDto getAccountById(Long id) throws NotFoundException;

    AccountResponseDto create(AccountRequestDto accountRequestDto) throws NotFoundException;

    AccountResponseDto update(Long id, AccountRequestDto accountRequestDto) throws NotFoundException;

    void delete(Long id) throws NotFoundException;
}
