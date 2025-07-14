package org.galymzhan.financetrackerbackend.service;

import org.galymzhan.financetrackerbackend.dto.request.AccountRequestDto;
import org.galymzhan.financetrackerbackend.dto.response.AccountResponseDto;

import java.util.List;

public interface AccountService {

    List<AccountResponseDto> getAll();

    AccountResponseDto getById(Long id);

    AccountResponseDto create(AccountRequestDto accountRequestDto);

    AccountResponseDto update(Long id, AccountRequestDto accountRequestDto);

    void delete(Long id);
}
