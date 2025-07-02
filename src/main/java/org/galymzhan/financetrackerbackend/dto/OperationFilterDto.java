package org.galymzhan.financetrackerbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.galymzhan.financetrackerbackend.entity.OperationType;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Filter criteria for operations")
public class OperationFilterDto {

    @Schema(description = "Filter by operation type", example = "EXPENSE")
    private OperationType operationType;

    @Schema(description = "Filter by category IDs", example = "[1, 2, 3]")
    private List<Long> categoryIds;

    @Schema(description = "Filter by account IDs (either accountIn or accountOut)", example = "[1, 2]")
    private List<Long> accountIds;

    @Schema(description = "Filter by tag IDs", example = "[1, 2]")
    private List<Long> tagIds;

    @Schema(description = "Minimum amount", example = "10.50")
    private BigDecimal minAmount;

    @Schema(description = "Maximum amount", example = "1000.00")
    private BigDecimal maxAmount;

    @Schema(description = "Start date (inclusive)", example = "2024-01-01")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @Schema(description = "End date (inclusive)", example = "2024-12-31")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    @Schema(description = "Search in operation name or note", example = "grocery")
    private String searchText;
}