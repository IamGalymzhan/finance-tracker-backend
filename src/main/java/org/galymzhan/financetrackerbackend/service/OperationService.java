package org.galymzhan.financetrackerbackend.service;

import org.galymzhan.financetrackerbackend.dto.filter.OperationFilterDto;
import org.galymzhan.financetrackerbackend.dto.request.OperationRequestDto;
import org.galymzhan.financetrackerbackend.dto.response.OperationResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OperationService {

    List<OperationResponseDto> getAll();

    Page<OperationResponseDto> getAllFiltered(OperationFilterDto filters, Pageable pageable);

    OperationResponseDto getById(Long id);

    OperationResponseDto create(OperationRequestDto operationRequestDto);

    void createBatch(List<OperationRequestDto> operationRequestDtos);

    OperationResponseDto update(Long id, OperationRequestDto operationRequestDto);

    void delete(Long id);
}
