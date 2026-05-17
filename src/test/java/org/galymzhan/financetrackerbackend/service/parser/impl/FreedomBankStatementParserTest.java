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

class FreedomBankStatementParserTest {

    private static final Long ACCOUNT_ID = 42L;

    private static List<ParsedOperationResponseDto> result;
    private static List<ParsedOperationResponseDto> resultWithInternal;

    @BeforeAll
    static void parseFixture() throws Exception {
        FreedomBankStatementParser parser = new FreedomBankStatementParser();
        byte[] content;
        try (InputStream in = FreedomBankStatementParserTest.class
                .getResourceAsStream("/parser/freedom_2.pdf")) {
            assertThat(in).as("fixture /parser/freedom_2.pdf on classpath").isNotNull();
            content = in.readAllBytes();
        }
        MockMultipartFile file = new MockMultipartFile(
                "file", "freedom_2.pdf", "application/pdf", content);
        result = parser.parseStatement(file, ACCOUNT_ID, false);
        MockMultipartFile fileAgain = new MockMultipartFile(
                "file", "freedom_2.pdf", "application/pdf", content);
        resultWithInternal = parser.parseStatement(fileAgain, ACCOUNT_ID, true);
    }

    @Test
    void supportsFreedomBank() {
        assertThat(new FreedomBankStatementParser().getSupportedBankType())
                .isEqualTo(BankType.FREEDOM_BANK);
    }

    @Test
    void parsesEveryTransactionFromTheFixture() {
        // Excluding internal deposit transfers (4 "Выплата вклада" rows in the fixture).
        assertThat(result).hasSize(166);
    }

    @Test
    void includesInternalTransfersWhenRequested() {
        assertThat(resultWithInternal).hasSize(170);
    }

    @Test
    void neverEmitsNullOperationType() {
        assertThat(result).allSatisfy(r -> assertThat(r.getOperationType()).isNotNull());
    }

    @Test
    void neverEmitsInternalTransfersByDefault() {
        assertThat(result)
                .extracting(ParsedOperationResponseDto::getOperationType)
                .doesNotContain(OperationType.TRANSFER);
    }

    @Test
    void internalTransfersAreTaggedTransferAndCreditTheAccount() {
        // "Выплата вклада" = money returning from own deposit → INCOME direction → accountIn.
        List<ParsedOperationResponseDto> transfers = resultWithInternal.stream()
                .filter(r -> r.getOperationType() == OperationType.TRANSFER)
                .toList();
        assertThat(transfers).hasSize(4);
        assertThat(transfers).allSatisfy(r -> {
            assertThat(r.getAccountInId()).isEqualTo(ACCOUNT_ID);
            assertThat(r.getAccountOutId()).isNull();
            assertThat(r.getName()).contains("Выплата вклада");
        });
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
    void parsesMultilineDetailsAcrossLines() {
        // Detail "SUPERMARKET GALMART NUR-SULTAN KZ" wraps onto a second visual line in the PDF;
        // the continuation row gets merged into the date-anchored row.
        assertRow(LocalDate.of(2026, 4, 30), "1760.00", OperationType.EXPENSE, "SUPERMARKET GALMART");
        assertRow(LocalDate.of(2026, 4, 30), "1760.00", OperationType.EXPENSE, "SULTAN KZ");
    }

    @Test
    void spotChecksKnownRows() {
        assertRow(LocalDate.of(2026, 4, 30), "1880.00", OperationType.EXPENSE, "YANDEX.GO ALMATY KZ");
        assertRow(LocalDate.of(2026, 4, 30), "350000.00", OperationType.INCOME, "90010160 ASTANA KZ");
        assertRow(LocalDate.of(2026, 4, 9), "50000.00", OperationType.EXPENSE, "90010029 ASTANA KZ");
        assertRow(LocalDate.of(2026, 4, 2), "30000.00", OperationType.EXPENSE, "Перевод с карты на карту");
        assertRow(LocalDate.of(2026, 4, 1), "6000.00", OperationType.EXPENSE, "AITMEN BARBERSHOP ASTANA Q. KZ");
    }

    @Test
    void parsesNonKztAmounts() {
        // 02.04.2026  -18.00 $  USD  Покупка  PEGASUS UK GB
        assertThat(result).anySatisfy(r -> {
            assertThat(r.getDate()).isEqualTo(LocalDate.of(2026, 4, 2));
            assertThat(r.getAmount()).isEqualByComparingTo("18.00");
            assertThat(r.getOperationType()).isEqualTo(OperationType.EXPENSE);
            assertThat(r.getName()).contains("PEGASUS");
        });
    }

    private static void assertRow(LocalDate date, String amount, OperationType type, String nameSubstring) {
        assertThat(result).anySatisfy(r -> {
            assertThat(r.getDate()).isEqualTo(date);
            assertThat(r.getAmount()).isEqualByComparingTo(amount);
            assertThat(r.getOperationType()).isEqualTo(type);
            assertThat(r.getName()).contains(nameSubstring);
        });
    }
}
