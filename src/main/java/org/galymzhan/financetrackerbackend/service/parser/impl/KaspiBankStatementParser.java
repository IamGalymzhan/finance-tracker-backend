package org.galymzhan.financetrackerbackend.service.parser.impl;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.galymzhan.financetrackerbackend.dto.response.ParsedOperationResponseDto;
import org.galymzhan.financetrackerbackend.entity.enums.BankType;
import org.galymzhan.financetrackerbackend.entity.enums.OperationType;
import org.galymzhan.financetrackerbackend.service.parser.BankStatementParser;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import technology.tabula.ObjectExtractor;
import technology.tabula.Page;
import technology.tabula.Table;
import technology.tabula.TextChunk;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class KaspiBankStatementParser implements BankStatementParser {

    private static final Map<String, OperationType> TRANSACTION_TO_TYPES = Map.of(
            "Transfers", OperationType.EXPENSE,
            "Purchases", OperationType.EXPENSE,
            "Replenishment", OperationType.INCOME
    );

    private static LocalDate stringToLocalDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy");
        return LocalDate.parse(date, formatter);
    }

    private static BigDecimal parseAmount(String amountStr) {
        if (amountStr == null || amountStr.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }

        String cleanAmount = amountStr.replace("₸", "").trim();

        boolean isNegative = cleanAmount.startsWith("-");
        if (isNegative) {
            cleanAmount = cleanAmount.substring(1).trim();
        }

        cleanAmount = cleanAmount.replace(" ", "");

        cleanAmount = cleanAmount.replace(",", ".");

        return new BigDecimal(cleanAmount);
    }

    @Override
    public BankType getSupportedBankType() {
        return BankType.KASPI_BANK;
    }

    @Override
    public List<ParsedOperationResponseDto> parseStatement(MultipartFile file, Long accountId) throws IOException {
        List<ParsedOperationResponseDto> result = new ArrayList<>();

        File tempFile = File.createTempFile("statement", ".pdf");
        file.transferTo(tempFile);
        PDDocument pdf = Loader.loadPDF(tempFile);
        ObjectExtractor extractor = new ObjectExtractor(pdf);
        SpreadsheetExtractionAlgorithm sea = new SpreadsheetExtractionAlgorithm();
        for (int pageNum = 1; pageNum <= pdf.getNumberOfPages(); pageNum++) {
            Page page = extractor.extract(pageNum);

            List<Table> tables = sea.extract(page);

            for (Table table : tables) {
                if (!Objects.equals(table.getRows().getFirst().getFirst().getText(), "Date") && pageNum == 1) {
                    continue;
                }
                table.getRows().stream()
                        .skip(1)
                        .forEach(row -> {
                            String dateStr = ((TextChunk) row.get(0).getTextElements().getFirst()).getText();
                            String amountStr = ((TextChunk) row.get(1).getTextElements().getFirst()).getText();
                            String transactionStr = ((TextChunk) row.get(2).getTextElements().getFirst()).getText();
                            String detailsStr = ((TextChunk) row.get(3).getTextElements().getFirst()).getText();

                            ParsedOperationResponseDto dto = new ParsedOperationResponseDto();
                            dto.setName(detailsStr);
                            dto.setAmount(parseAmount(amountStr));
                            dto.setOperationType(TRANSACTION_TO_TYPES.get(transactionStr));
                            dto.setDate(stringToLocalDate(dateStr));
                            if (dto.getOperationType() == OperationType.EXPENSE) {
                                dto.setAccountOutId(accountId);
                            } else if (dto.getOperationType() == OperationType.INCOME) {
                                dto.setAccountInId(accountId);
                            }

                            result.add(dto);
                        });
            }
        }
        pdf.close();

        return result;
    }
}
