package org.galymzhan.financetrackerbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class AccountRequestDto {
    @JsonProperty("name")
    private String name;

    @JsonProperty("accountType")
    private String accountType;

    @JsonProperty("balance")
    private BigDecimal balance;

    @JsonProperty("color")
    private String color;

    @JsonProperty("icon")
    private String icon;
}
