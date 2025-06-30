package org.galymzhan.financetrackerbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import org.galymzhan.financetrackerbackend.entity.OperationType;

import java.math.BigDecimal;
import java.util.Set;

@Value
@Builder
public class OperationResponseDto {

    @JsonProperty("id")
    Long id;

    @JsonProperty("name")
    String name;

    @JsonProperty("operationType")
    OperationType operationType;

    @JsonProperty("category")
    CategorySummaryDto category;

    @JsonProperty("amount")
    BigDecimal amount;

    @JsonProperty("accountIn")
    AccountSummaryDto accountIn;

    @JsonProperty("accountOut")
    AccountSummaryDto accountOut;

    @JsonProperty("note")
    String note;

    @JsonProperty("tags")
    Set<TagSummaryDto> tags;

    @Value
    @Builder
    public static class AccountSummaryDto {
        @JsonProperty("id")
        Long id;

        @JsonProperty("name")
        String name;

        @JsonProperty("accountType")
        String accountType;
    }

    @Value
    @Builder
    public static class CategorySummaryDto {
        @JsonProperty("id")
        Long id;

        @JsonProperty("name")
        String name;
    }

    @Value
    @Builder
    public static class TagSummaryDto {
        @JsonProperty("id")
        Long id;

        @JsonProperty("name")
        String name;
    }
}
