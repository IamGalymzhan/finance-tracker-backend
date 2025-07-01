package org.galymzhan.financetrackerbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
@Schema(description = "Account creation/update request")
public class AccountRequestDto {

    @Size(min = 1, max = 100)
    @JsonProperty("name")
    @Schema(description = "Account name", example = "Main Checking Account", maxLength = 100)
    String name;

    @JsonProperty("accountType")
    @Schema(description = "Account type", example = "DEBIT", allowableValues = {"DEBIT", "CASH", "DEPOSIT", "SAVING"})
    String accountType;

    @PositiveOrZero
    @JsonProperty("balance")
    @Schema(description = "Initial account balance", example = "1500.00", minimum = "0")
    BigDecimal balance;

    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$")
    @JsonProperty("color")
    @Schema(description = "Account color in hex format", example = "#3498db", pattern = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$")
    String color;

    @JsonProperty("icon")
    @Schema(description = "Account icon identifier", example = "bank")
    String icon;
}
