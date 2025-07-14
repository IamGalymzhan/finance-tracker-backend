package org.galymzhan.financetrackerbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationCsvExportDto {

    private LocalDate date;

    private String description;

    private BigDecimal amount;

    private String type;

    private String category;

    private String accountIn;

    private String accountOut;

    private String tags;

    private String note;

    private LocalDateTime createdDate;
}