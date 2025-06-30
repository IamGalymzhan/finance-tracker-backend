package org.galymzhan.financetrackerbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class AccountRequestDto {

    @Size(min = 1, max = 100)
    @JsonProperty("name")
    String name;

    @JsonProperty("accountType")
    String accountType;

    @PositiveOrZero
    @JsonProperty("balance")
    BigDecimal balance;

    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$")
    @JsonProperty("color")
    String color;

    @JsonProperty("icon")
    String icon;
}
