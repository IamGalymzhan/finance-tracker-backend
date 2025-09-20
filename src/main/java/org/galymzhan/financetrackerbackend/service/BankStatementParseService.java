package org.galymzhan.financetrackerbackend.service;

import org.galymzhan.financetrackerbackend.dto.response.ParsedOperationResponseDto;
import org.galymzhan.financetrackerbackend.entity.enums.BankType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface BankStatementParseService {
    List<ParsedOperationResponseDto> parseStatement(MultipartFile file, BankType bankType, Long accountId) throws IOException;
}
