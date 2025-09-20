package org.galymzhan.financetrackerbackend.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.galymzhan.financetrackerbackend.dto.response.ParsedOperationResponseDto;
import org.galymzhan.financetrackerbackend.entity.enums.BankType;
import org.galymzhan.financetrackerbackend.service.BankStatementParseService;
import org.galymzhan.financetrackerbackend.service.parser.BankStatementParser;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class BankStatementParseServiceImpl implements BankStatementParseService {


    private final List<BankStatementParser> bankStatementParsers;

    private Map<BankType, BankStatementParser> parsers;

    @PostConstruct
    private void initializeParsers() {
        this.parsers = bankStatementParsers.stream()
                .collect(Collectors.toMap(BankStatementParser::getSupportedBankType, Function.identity()));
    }

    @Override
    public List<ParsedOperationResponseDto> parseStatement(MultipartFile file, BankType bankType, Long accountId) throws IOException {
        BankStatementParser parser = parsers.get(bankType);
        if (parser == null) {
            throw new IllegalArgumentException("Unsupported bank type: " + bankType);
        }

        return parser.parseStatement(file, accountId);
    }
}
