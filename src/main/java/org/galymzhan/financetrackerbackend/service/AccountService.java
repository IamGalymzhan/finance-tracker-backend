package org.galymzhan.financetrackerbackend.service;

import org.galymzhan.financetrackerbackend.dto.AccountRequestDto;
import org.galymzhan.financetrackerbackend.dto.AccountResponseDto;

import java.util.List;

public interface AccountService {

    List<AccountResponseDto> getAll();

    AccountResponseDto getById(Long id);

    AccountResponseDto create(AccountRequestDto accountRequestDto);

    AccountResponseDto update(Long id, AccountRequestDto accountRequestDto);

    void delete(Long id);
}
