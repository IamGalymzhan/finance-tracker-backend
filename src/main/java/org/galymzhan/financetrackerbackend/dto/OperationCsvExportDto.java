package org.galymzhan.financetrackerbackend.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Value
@Builder
public class OperationCsvExportDto {

    LocalDate date;

    String description;

    BigDecimal amount;

    String type;

    String category;

    String accountIn;

    String accountOut;

    String tags;

    String note;
    
    LocalDateTime createdDate;
}