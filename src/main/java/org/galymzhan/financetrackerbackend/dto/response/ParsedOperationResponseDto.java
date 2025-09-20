package org.galymzhan.financetrackerbackend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.galymzhan.financetrackerbackend.entity.enums.OperationType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParsedOperationResponseDto {

    @JsonProperty("name")
    private String name;

    @JsonProperty("operationType")
    private OperationType operationType;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("date")
    private LocalDate date;

    @JsonProperty("accountInId")
    private Long accountInId;

    @JsonProperty("accountOutId")
    private Long accountOutId;

}
