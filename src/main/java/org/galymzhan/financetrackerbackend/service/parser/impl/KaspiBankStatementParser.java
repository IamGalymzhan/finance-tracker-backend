package org.galymzhan.financetrackerbackend.service.parser.impl;

import lombok.extern.slf4j.Slf4j;
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
import technology.tabula.RectangularTextContainer;
import technology.tabula.Table;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

@Slf4j
@Service
public class KaspiBankStatementParser implements BankStatementParser {

    private static final Map<String, OperationType> OPERATION_TYPES = Map.of(
            "Покупка", OperationType.EXPENSE,
            "Перевод", OperationType.EXPENSE,
            "Снятие", OperationType.EXPENSE,
            "Пополнение", OperationType.INCOME
    );

    private static final Map<String, OperationType> INTERNAL_TRANSFER_DIRECTIONS = Map.of(
            "Перевод на свой счет", OperationType.EXPENSE,
            "Поступление со своего счета", OperationType.INCOME
    );

    private static final Set<String> HEADER_FIRST_CELLS = Set.of("Дата", "Date");

    private static final Pattern DATE_LIKE = Pattern.compile("\\d{2}\\.\\d{2}\\.\\d{2}");

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy");

    @Override
    public BankType getSupportedBankType() {
        return BankType.KASPI_BANK;
    }

    @Override
    public List<ParsedOperationResponseDto> parseStatement(MultipartFile file, Long accountId, boolean includeInternalTransfers) throws IOException {
        List<ParsedOperationResponseDto> result = new ArrayList<>();

        File tempFile = File.createTempFile("statement", ".pdf");
        try {
            file.transferTo(tempFile);
            try (PDDocument pdf = Loader.loadPDF(tempFile)) {
                ObjectExtractor extractor = new ObjectExtractor(pdf);
                SpreadsheetExtractionAlgorithm sea = new SpreadsheetExtractionAlgorithm();
                boolean headerSeen = false;

                for (int pageNum = 1; pageNum <= pdf.getNumberOfPages(); pageNum++) {
                    Page page = extractor.extract(pageNum);
                    List<Table> tables = sea.extract(page);

                    for (Table table : tables) {
                        int startRow = transactionTableStartRow(table, headerSeen);
                        if (startRow < 0) {
                            continue;
                        }
                        if (startRow == 1) {
                            headerSeen = true;
                        }
                        List<List<RectangularTextContainer>> rows = table.getRows();
                        for (int i = startRow; i < rows.size(); i++) {
                            List<RectangularTextContainer> row = rows.get(i);
                            parseRow(row, accountId, pageNum, includeInternalTransfers).ifPresent(result::add);
                        }
                    }
                }
            }
        } finally {
            Files.deleteIfExists(tempFile.toPath());
        }

        return result;
    }

    private static int transactionTableStartRow(Table table, boolean headerSeen) {
        List<List<RectangularTextContainer>> rows = table.getRows();
        if (rows.isEmpty() || table.getColCount() < 4) {
            return -1;
        }
        String firstCell = cellText(rows.getFirst(), 0);
        if (HEADER_FIRST_CELLS.contains(firstCell)) {
            return 1;
        }
        if (headerSeen && table.getColCount() == 4 && DATE_LIKE.matcher(firstCell).matches()) {
            return 0;
        }
        return -1;
    }

    private static java.util.Optional<ParsedOperationResponseDto> parseRow(
            List<RectangularTextContainer> row, Long accountId, int pageNum, boolean includeInternalTransfers) {
        String dateStr = cellText(row, 0);
        String amountStr = cellText(row, 1);
        String transactionStr = cellText(row, 2);
        String detailsStr = cellText(row, 3);

        OperationType internalDirection = INTERNAL_TRANSFER_DIRECTIONS.get(transactionStr);
        if (internalDirection != null) {
            if (!includeInternalTransfers) {
                return java.util.Optional.empty();
            }
            return buildDto(dateStr, amountStr, transactionStr, detailsStr,
                    OperationType.TRANSFER, internalDirection, accountId, pageNum);
        }

        OperationType type = OPERATION_TYPES.get(transactionStr);
        if (type == null) {
            log.warn("Skipping row on page {} with unknown operation '{}': date={} amount={} details={}",
                    pageNum, transactionStr, dateStr, amountStr, detailsStr);
            return java.util.Optional.empty();
        }

        return buildDto(dateStr, amountStr, transactionStr, detailsStr, type, type, accountId, pageNum);
    }

    private static java.util.Optional<ParsedOperationResponseDto> buildDto(
            String dateStr, String amountStr, String transactionStr, String detailsStr,
            OperationType type, OperationType direction, Long accountId, int pageNum) {
        try {
            ParsedOperationResponseDto dto = new ParsedOperationResponseDto();
            dto.setName(detailsStr);
            dto.setAmount(parseAmount(amountStr));
            dto.setOperationType(type);
            dto.setDate(LocalDate.parse(dateStr, DATE_FORMATTER));
            if (direction == OperationType.EXPENSE) {
                dto.setAccountOutId(accountId);
            } else {
                dto.setAccountInId(accountId);
            }
            return java.util.Optional.of(dto);
        } catch (RuntimeException e) {
            log.warn("Skipping malformed row on page {} ({}): date='{}' amount='{}' op='{}' details='{}'",
                    pageNum, e.getMessage(), dateStr, amountStr, transactionStr, detailsStr);
            return java.util.Optional.empty();
        }
    }

    private static String cellText(List<RectangularTextContainer> row, int index) {
        if (index >= row.size()) {
            return "";
        }
        String text = row.get(index).getText();
        if (text == null) {
            return "";
        }
        return text
                .replace(' ', ' ')
                .replace("​", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private static BigDecimal parseAmount(String amountStr) {
        if (amountStr == null || amountStr.isBlank()) {
            return BigDecimal.ZERO;
        }
        String cleanAmount = amountStr.replace("₸", "").trim();
        if (cleanAmount.startsWith("-") || cleanAmount.startsWith("+")) {
            cleanAmount = cleanAmount.substring(1).trim();
        }
        int parenIdx = cleanAmount.indexOf('(');
        if (parenIdx >= 0) {
            cleanAmount = cleanAmount.substring(0, parenIdx).trim();
        }
        cleanAmount = cleanAmount.replace(" ", "").replace(",", ".");
        return new BigDecimal(cleanAmount);
    }
}
