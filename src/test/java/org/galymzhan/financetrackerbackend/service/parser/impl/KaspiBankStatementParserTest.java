package org.galymzhan.financetrackerbackend.service.parser.impl;

import org.galymzhan.financetrackerbackend.dto.response.ParsedOperationResponseDto;
import org.galymzhan.financetrackerbackend.entity.enums.BankType;
import org.galymzhan.financetrackerbackend.entity.enums.OperationType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class KaspiBankStatementParserTest {

    private static final Long ACCOUNT_ID = 42L;

    private static List<ParsedOperationResponseDto> result;

    @BeforeAll
    static void parseFixture() throws Exception {
        KaspiBankStatementParser parser = new KaspiBankStatementParser();
        byte[] content;
        try (InputStream in = KaspiBankStatementParserTest.class
                .getResourceAsStream("/parser/kaspi_2.pdf")) {
            assertThat(in).as("fixture /parser/kaspi_2.pdf on classpath").isNotNull();
            content = in.readAllBytes();
        }
        MockMultipartFile file = new MockMultipartFile(
                "file", "kaspi_2.pdf", "application/pdf", content);
        result = parser.parseStatement(file, ACCOUNT_ID, false);
    }

    @Test
    void supportsKaspiBank() {
        assertThat(new KaspiBankStatementParser().getSupportedBankType())
                .isEqualTo(BankType.KASPI_BANK);
    }

    @Test
    void parsesEveryTransactionFromTheFixture() {
        assertThat(result).hasSize(86);
    }

    @Test
    void neverEmitsNullOperationType() {
        assertThat(result).allSatisfy(r -> assertThat(r.getOperationType()).isNotNull());
    }

    @Test
    void neverEmitsInternalTransfers() {
        assertThat(result)
                .extracting(ParsedOperationResponseDto::getName)
                .doesNotContain("На Kaspi Депозит")
                .doesNotContain("С Kaspi Депозита");
    }

    @Test
    void expenseRowsSetAccountOutOnly() {
        assertThat(result)
                .filteredOn(r -> r.getOperationType() == OperationType.EXPENSE)
                .allSatisfy(r -> {
                    assertThat(r.getAccountOutId()).isEqualTo(ACCOUNT_ID);
                    assertThat(r.getAccountInId()).isNull();
                });
    }

    @Test
    void incomeRowsSetAccountInOnly() {
        assertThat(result)
                .filteredOn(r -> r.getOperationType() == OperationType.INCOME)
                .allSatisfy(r -> {
                    assertThat(r.getAccountInId()).isEqualTo(ACCOUNT_ID);
                    assertThat(r.getAccountOutId()).isNull();
                });
    }

    @Test
    void amountsArePositive() {
        assertThat(result).allSatisfy(r ->
                assertThat(r.getAmount()).isGreaterThan(BigDecimal.ZERO));
    }

    @Test
    void datesSpanTheStatementPeriod() {
        assertThat(result).extracting(ParsedOperationResponseDto::getDate)
                .allMatch(d -> !d.isBefore(LocalDate.of(2026, 4, 1))
                        && !d.isAfter(LocalDate.of(2026, 4, 30)));
        assertThat(result).extracting(ParsedOperationResponseDto::getDate)
                .contains(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 30));
    }

    @Test
    void picksUpFirstRowOnContinuationPages() {
        // First data row on page 3 (28.04.26 - 150,00 ₸ Покупка Аппарат самообслуживания)
        // would be dropped by the old unconditional skip(1). Make sure we keep it.
        assertThat(findRow(LocalDate.of(2026, 4, 28), new BigDecimal("150.00"), "Аппарат самообслуживания"))
                .isNotNull();
        // First data row on page 4 (18.04.26 - 110,00 ₸ Покупка Avtobys. Оплата проезда по QR).
        // There are several 18.04 / 110 / Avtobys rows; assert at least one is there.
        assertThat(result).anySatisfy(r -> {
            assertThat(r.getDate()).isEqualTo(LocalDate.of(2026, 4, 18));
            assertThat(r.getAmount()).isEqualByComparingTo("110.00");
            assertThat(r.getName()).contains("Avtobys");
        });
    }

    @Test
    void spotChecksKnownRows() {
        assertRow(LocalDate.of(2026, 4, 30), "200000.00", OperationType.EXPENSE, "Банкомат Mega Silk Way");
        assertRow(LocalDate.of(2026, 4, 30), "15500.00", OperationType.INCOME, "Дарига А.");
        assertRow(LocalDate.of(2026, 4, 16), "3800.92", OperationType.EXPENSE, "Spotify AB");
        assertRow(LocalDate.of(2026, 4, 1), "2280.00", OperationType.EXPENSE, "GC Astana Hub");
        assertRow(LocalDate.of(2026, 4, 18), "100000.00", OperationType.EXPENSE, "Банкомат Mega Silk Way");
    }

    private static ParsedOperationResponseDto findRow(LocalDate date, BigDecimal amount, String nameSubstring) {
        return result.stream()
                .filter(r -> r.getDate().equals(date))
                .filter(r -> r.getAmount().compareTo(amount) == 0)
                .filter(r -> r.getName() != null && r.getName().contains(nameSubstring))
                .findFirst()
                .orElse(null);
    }

    private static void assertRow(LocalDate date, String amount, OperationType type, String name) {
        assertThat(result).anySatisfy(r -> {
            assertThat(r.getDate()).isEqualTo(date);
            assertThat(r.getAmount()).isEqualByComparingTo(amount);
            assertThat(r.getOperationType()).isEqualTo(type);
            assertThat(r.getName()).isEqualTo(name);
        });
    }
}
