package org.galymzhan.financetrackerbackend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.galymzhan.financetrackerbackend.entity.enums.AccountType;
import org.galymzhan.financetrackerbackend.entity.enums.OperationType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationResponseDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("operationType")
    private OperationType operationType;

    @JsonProperty("category")
    private CategorySummaryDto category;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("date")
    private LocalDate date;

    @JsonProperty("accountIn")
    private AccountSummaryDto accountIn;

    @JsonProperty("accountOut")
    private AccountSummaryDto accountOut;

    @JsonProperty("note")
    private String note;

    @JsonProperty("tags")
    private Set<TagSummaryDto> tags;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountSummaryDto {
        @JsonProperty("id")
        private Long id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("accountType")
        private AccountType accountType;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategorySummaryDto {
        @JsonProperty("id")
        private Long id;

        @JsonProperty("name")
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TagSummaryDto {
        @JsonProperty("id")
        private Long id;

        @JsonProperty("name")
        private String name;
    }
}
