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
import technology.tabula.extractors.BasicExtractionAlgorithm;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class FreedomBankStatementParser implements BankStatementParser {

    // Listed longest-first so longest-prefix wins (e.g. "Платеж по кредиту" before "Платеж").
    private static final List<String> OPERATION_LABELS = List.of(
            "Платеж по кредиту",
            "Пополнение",
            "Погашение",
            "Овердрафт",
            "Покупка",
            "Перевод",
            "Платеж",
            "Снятие",
            "Другое"
    );

    private static final Map<String, OperationType> OPERATION_TYPES = Map.ofEntries(
            Map.entry("Покупка", OperationType.EXPENSE),
            Map.entry("Перевод", OperationType.EXPENSE),
            Map.entry("Платеж", OperationType.EXPENSE),
            Map.entry("Платеж по кредиту", OperationType.EXPENSE),
            Map.entry("Снятие", OperationType.EXPENSE),
            Map.entry("Погашение", OperationType.EXPENSE),
            Map.entry("Овердрафт", OperationType.EXPENSE),
            Map.entry("Пополнение", OperationType.INCOME)
            // "Другое" is resolved by amount sign at parse time.
    );

    private static final Pattern DATE_PATTERN = Pattern.compile("^(\\d{2}\\.\\d{2}\\.\\d{4})(?=\\s|$)");
    private static final Pattern AMOUNT_PATTERN = Pattern.compile("([+-])\\s*([\\d\\s.,]*\\d)\\s*[₸$€]");
    private static final Pattern CURRENCY_CODE = Pattern.compile("\\b(KZT|USD|EUR)\\b");
    private static final Set<String> HEADER_CELLS = Set.of("Дата", "Date");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private static final String DEPOSIT_IN_MARKER = "Выплата вклада";
    private static final String DEPOSIT_OUT_MARKER = "Прием вклада";

    private static final double FOOTER_Y = 720.0;

    @Override
    public BankType getSupportedBankType() {
        return BankType.FREEDOM_BANK;
    }

    @Override
    public List<ParsedOperationResponseDto> parseStatement(MultipartFile file, Long accountId, boolean includeInternalTransfers) throws IOException {
        File tempFile = File.createTempFile("statement", ".pdf");
        try {
            file.transferTo(tempFile);
            try (PDDocument pdf = Loader.loadPDF(tempFile)) {
                List<RawRow> raw = extractRows(pdf);
                return assemble(raw, accountId, includeInternalTransfers);
            }
        } finally {
            Files.deleteIfExists(tempFile.toPath());
        }
    }

    private List<RawRow> extractRows(PDDocument pdf) {
        ObjectExtractor extractor = new ObjectExtractor(pdf);
        BasicExtractionAlgorithm bea = new BasicExtractionAlgorithm();
        List<RawRow> result = new ArrayList<>();

        for (int pageNum = 1; pageNum <= pdf.getNumberOfPages(); pageNum++) {
            Page page = extractor.extract(pageNum);
            for (Table table : bea.extract(page)) {
                int headerRow = findHeaderRow(table);
                if (headerRow < 0) {
                    continue;
                }
                List<List<RectangularTextContainer>> rows = table.getRows();
                for (int i = headerRow + 1; i < rows.size(); i++) {
                    Optional<RawRow> raw = toRawRow(rows.get(i), pageNum);
                    raw.ifPresent(result::add);
                }
            }
        }
        result.sort(Comparator.<RawRow>comparingInt(r -> r.pageNum).thenComparingDouble(r -> r.y));
        return result;
    }

    private static int findHeaderRow(Table table) {
        List<List<RectangularTextContainer>> rows = table.getRows();
        for (int i = 0; i < rows.size(); i++) {
            for (RectangularTextContainer cell : rows.get(i)) {
                if (HEADER_CELLS.contains(normalize(cell.getText()))) {
                    return i;
                }
            }
        }
        return -1;
    }

    private static Optional<RawRow> toRawRow(List<RectangularTextContainer> cells, int pageNum) {
        StringBuilder text = new StringBuilder();
        double y = 0.0;
        for (RectangularTextContainer cell : cells) {
            String t = normalize(cell.getText());
            if (t.isEmpty()) {
                continue;
            }
            if (text.length() > 0) {
                text.append(' ');
            }
            text.append(t);
            y = Math.max(y, cell.getY());
        }
        if (text.length() == 0 || y >= FOOTER_Y) {
            return Optional.empty();
        }
        return Optional.of(new RawRow(pageNum, y, text.toString()));
    }

    private List<ParsedOperationResponseDto> assemble(List<RawRow> rows, Long accountId, boolean includeInternalTransfers) {
        List<ParsedRow> parsed = new ArrayList<>(rows.size());
        for (RawRow r : rows) {
            parsed.add(parseRow(r));
        }

        List<Integer> anchorIdx = new ArrayList<>();
        for (int i = 0; i < parsed.size(); i++) {
            if (parsed.get(i).date != null) {
                anchorIdx.add(i);
            }
        }

        // Cross-page split: when an anchor lacks an amount, pull it from the next
        // amount-only continuation row (typical when one transaction wraps a page break).
        for (int idx : anchorIdx) {
            ParsedRow anchor = parsed.get(idx);
            if (anchor.amount != null) {
                continue;
            }
            for (int j = idx + 1; j < parsed.size(); j++) {
                ParsedRow r = parsed.get(j);
                if (r.date != null) {
                    break;
                }
                if (r.amount != null) {
                    parsed.set(idx, anchor.withAmount(r.amount));
                    parsed.set(j, r.withoutAmount());
                    break;
                }
            }
        }

        Map<Integer, List<ParsedRow>> extraDetails = new LinkedHashMap<>();
        for (int i = 0; i < parsed.size(); i++) {
            ParsedRow row = parsed.get(i);
            if (row.date != null || row.details.isEmpty()) {
                continue;
            }
            int nearest = nearestAnchor(parsed, anchorIdx, i);
            if (nearest >= 0) {
                extraDetails.computeIfAbsent(nearest, k -> new ArrayList<>()).add(row);
            }
        }

        List<ParsedOperationResponseDto> result = new ArrayList<>();
        for (int i : anchorIdx) {
            ParsedRow anchor = parsed.get(i);
            buildDto(anchor, extraDetails.get(i), accountId, includeInternalTransfers)
                    .ifPresent(result::add);
        }
        return result;
    }

    private static int nearestAnchor(List<ParsedRow> parsed, List<Integer> anchorIdx, int rowIdx) {
        if (anchorIdx.isEmpty()) {
            return -1;
        }
        ParsedRow row = parsed.get(rowIdx);
        int best = -1;
        double bestDist = Double.MAX_VALUE;
        for (int idx : anchorIdx) {
            ParsedRow anchor = parsed.get(idx);
            if (anchor.pageNum != row.pageNum) {
                continue;
            }
            double dist = Math.abs(anchor.y - row.y);
            if (dist < bestDist) {
                bestDist = dist;
                best = idx;
            }
        }
        return best;
    }

    private ParsedRow parseRow(RawRow raw) {
        String text = raw.text;

        LocalDate date = null;
        Matcher dateMatcher = DATE_PATTERN.matcher(text);
        if (dateMatcher.find()) {
            try {
                date = LocalDate.parse(dateMatcher.group(1), DATE_FORMATTER);
                text = text.substring(dateMatcher.end()).trim();
            } catch (RuntimeException e) {
                log.warn("Unparseable date on page {}: {}", raw.pageNum, raw.text);
            }
        }

        BigDecimal amount = null;
        Matcher amountMatcher = AMOUNT_PATTERN.matcher(text);
        if (amountMatcher.find()) {
            try {
                String sign = amountMatcher.group(1);
                String digits = amountMatcher.group(2).replaceAll("[\\s\\u00a0,]", "");
                amount = new BigDecimal(sign + digits);
                text = (text.substring(0, amountMatcher.start()) + text.substring(amountMatcher.end())).trim();
            } catch (RuntimeException e) {
                log.warn("Unparseable amount on page {}: {}", raw.pageNum, raw.text);
            }
        }

        Matcher currencyMatcher = CURRENCY_CODE.matcher(text);
        if (currencyMatcher.find()) {
            text = (text.substring(0, currencyMatcher.start()) + text.substring(currencyMatcher.end())).trim();
        }

        String operationLabel = null;
        if (date != null) {
            for (String label : OPERATION_LABELS) {
                if (text.startsWith(label)) {
                    operationLabel = label;
                    text = text.substring(label.length()).trim();
                    break;
                }
            }
        }

        return new ParsedRow(raw.pageNum, raw.y, date, amount, operationLabel, text);
    }

    private Optional<ParsedOperationResponseDto> buildDto(ParsedRow anchor, List<ParsedRow> continuations,
                                                          Long accountId, boolean includeInternalTransfers) {
        if (anchor.amount == null) {
            log.warn("Skipping row on page {} missing amount: date={} op={} details='{}'",
                    anchor.pageNum, anchor.date, anchor.operationLabel, anchor.details);
            return Optional.empty();
        }
        if (anchor.operationLabel == null) {
            log.warn("Skipping row on page {} with unknown operation: date={} amount={} text='{}'",
                    anchor.pageNum, anchor.date, anchor.amount, anchor.details);
            return Optional.empty();
        }

        String details = composeDetails(anchor, continuations);

        boolean isDepositIn = details.contains(DEPOSIT_IN_MARKER);
        boolean isDepositOut = details.contains(DEPOSIT_OUT_MARKER);
        boolean isInternalTransfer = isDepositIn || isDepositOut;

        OperationType type;
        OperationType direction;
        if (isInternalTransfer) {
            if (!includeInternalTransfers) {
                return Optional.empty();
            }
            type = OperationType.TRANSFER;
            direction = isDepositIn ? OperationType.INCOME : OperationType.EXPENSE;
        } else if ("Другое".equals(anchor.operationLabel)) {
            type = anchor.amount.signum() >= 0 ? OperationType.INCOME : OperationType.EXPENSE;
            direction = type;
        } else {
            type = OPERATION_TYPES.get(anchor.operationLabel);
            direction = type;
        }

        ParsedOperationResponseDto dto = new ParsedOperationResponseDto();
        dto.setDate(anchor.date);
        dto.setAmount(anchor.amount.abs());
        dto.setOperationType(type);
        dto.setName(details);
        if (direction == OperationType.EXPENSE) {
            dto.setAccountOutId(accountId);
        } else {
            dto.setAccountInId(accountId);
        }
        return Optional.of(dto);
    }

    private static String composeDetails(ParsedRow anchor, List<ParsedRow> continuations) {
        List<ParsedRow> all = new ArrayList<>();
        if (!anchor.details.isEmpty()) {
            all.add(anchor);
        }
        if (continuations != null) {
            all.addAll(continuations);
        }
        all.sort(Comparator.comparingDouble(r -> r.y));
        StringBuilder sb = new StringBuilder();
        for (ParsedRow r : all) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(r.details);
        }
        return sb.toString();
    }

    private static String normalize(String text) {
        if (text == null) {
            return "";
        }
        return text
                .replace(' ', ' ')
                .replace("​", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private record RawRow(int pageNum, double y, String text) {
    }

    private static final class ParsedRow {
        final int pageNum;
        final double y;
        final LocalDate date;
        final BigDecimal amount;
        final String operationLabel;
        final String details;

        ParsedRow(int pageNum, double y, LocalDate date, BigDecimal amount, String operationLabel, String details) {
            this.pageNum = pageNum;
            this.y = y;
            this.date = date;
            this.amount = amount;
            this.operationLabel = operationLabel;
            this.details = details == null ? "" : details;
        }

        ParsedRow withAmount(BigDecimal amount) {
            return new ParsedRow(pageNum, y, date, amount, operationLabel, details);
        }

        ParsedRow withoutAmount() {
            return new ParsedRow(pageNum, y, date, null, operationLabel, details);
        }
    }
}
