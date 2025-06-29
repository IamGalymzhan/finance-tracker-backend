package org.galymzhan.financetrackerbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class AccountRequestDto {

    @NotBlank(message = "Account name is required")
    @JsonProperty("name")
    String name;

    @NotBlank(message = "Account type is required")
    @JsonProperty("accountType")
    String accountType;

    @NotNull(message = "Balance is required")
    @JsonProperty("balance")
    BigDecimal balance;

    @JsonProperty("color")
    String color;

    @JsonProperty("icon")
    String icon;
}
