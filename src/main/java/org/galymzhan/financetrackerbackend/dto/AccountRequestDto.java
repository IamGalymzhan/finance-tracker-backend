package org.galymzhan.financetrackerbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class AccountRequestDto {

    @JsonProperty("name")
    String name;

    @JsonProperty("accountType")
    String accountType;

    @JsonProperty("balance")
    BigDecimal balance;

    @JsonProperty("color")
    String color;

    @JsonProperty("icon")
    String icon;
}
