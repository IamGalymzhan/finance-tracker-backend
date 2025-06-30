package org.galymzhan.financetrackerbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import org.galymzhan.financetrackerbackend.entity.OperationType;

import java.math.BigDecimal;
import java.util.Set;

@Value
@Builder
public class OperationRequestDto {

    @Size(min = 1, max = 100)
    @JsonProperty("name")
    String name;

    @JsonProperty("operationType")
    OperationType operationType;

    @JsonProperty("categoryId")
    Long categoryId;

    @PositiveOrZero
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
