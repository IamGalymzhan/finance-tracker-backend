package org.galymzhan.financetrackerbackend.service.parser;

import org.galymzhan.financetrackerbackend.dto.response.ParsedOperationResponseDto;
import org.galymzhan.financetrackerbackend.entity.enums.BankType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface BankStatementParser {

    BankType getSupportedBankType();

    List<ParsedOperationResponseDto> parseStatement(MultipartFile file, Long accountId) throws IOException;
}
