package org.galymzhan.financetrackerbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import org.galymzhan.financetrackerbackend.entity.OperationType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Value
@Builder
@Schema(description = "Operation creation/update request")
public class OperationRequestDto {

    @Size(min = 1, max = 100)
    @JsonProperty("name")
    @Schema(description = "Operation description", example = "Grocery shopping", maxLength = 100)
    String name;

    @JsonProperty("operationType")
    @Schema(description = "Type of operation", example = "EXPENSE", allowableValues = {"INCOME", "EXPENSE", "TRANSFER"})
    OperationType operationType;

    @JsonProperty("categoryId")
    @Schema(description = "Category ID for the operation", example = "1")
    Long categoryId;

    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @JsonProperty("amount")
    @Schema(description = "Operation amount", example = "45.50", minimum = "0")
    BigDecimal amount;

    @JsonProperty("date")
    @Schema(description = "Operation date", example = "2024-01-01")
    LocalDate date;

    @JsonProperty("accountInId")
    @Schema(description = "Destination account ID (for income/transfer)", example = "2")
    Long accountInId;

    @JsonProperty("accountOutId")
    @Schema(description = "Source account ID (for expense/transfer)", example = "1")
    Long accountOutId;

    @JsonProperty("note")
    @Schema(description = "Additional notes about the operation", example = "Weekly grocery shopping at Walmart")
    String note;

    @JsonProperty("tagIds")
    @Schema(description = "Set of tag IDs for labeling", example = "[1, 3, 5]")
    Set<Long> tagIds;
}
