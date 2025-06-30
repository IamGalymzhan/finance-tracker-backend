package org.galymzhan.financetrackerbackend.service;

import org.galymzhan.financetrackerbackend.dto.OperationRequestDto;
import org.galymzhan.financetrackerbackend.dto.OperationResponseDto;

import java.util.List;

public interface OperationService {

    List<OperationResponseDto> getAll();

    OperationResponseDto getById(Long id);

    OperationResponseDto create(OperationRequestDto operationRequestDto);

    OperationResponseDto update(Long id, OperationRequestDto operationRequestDto);

    void delete(Long id);
}
