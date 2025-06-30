package org.galymzhan.financetrackerbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Value;
import org.galymzhan.financetrackerbackend.entity.OperationType;

import java.math.BigDecimal;
import java.util.Set;

@Value
@Builder
public class OperationRequestDto {

    @JsonProperty("name")
    String name;

    @JsonProperty("operationType")
    OperationType operationType;

    @JsonProperty("categoryId")
    Long categoryId;

    @Positive(message = "Amount must be positive")
    @JsonProperty("amount")
    BigDecimal amount;

    @JsonProperty("accountInId")
    Long accountInId;

    @JsonProperty("accountOutId")
    Long accountOutId;

    @JsonProperty("note")
    String note;

    @JsonProperty("tagIds")
    Set<Long> tagIds;
}
