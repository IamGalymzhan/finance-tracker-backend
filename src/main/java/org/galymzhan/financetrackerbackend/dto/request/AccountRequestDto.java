package org.galymzhan.financetrackerbackend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Account creation/update request")
public class AccountRequestDto {

    @Size(min = 1, max = 100)
    @JsonProperty("name")
    @Schema(description = "Account name", example = "Main Checking Account", maxLength = 100)
    private String name;

    @JsonProperty("accountType")
    @Schema(description = "Account type", example = "DEBIT", allowableValues = {"DEBIT", "CASH", "DEPOSIT", "SAVING"})
    private String accountType;

    @PositiveOrZero
    @JsonProperty("balance")
    @Schema(description = "Initial account balance", example = "1500.00", minimum = "0")
    private BigDecimal balance;

    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$")
    @JsonProperty("color")
    @Schema(description = "Account color in hex format", example = "#3498db", pattern = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$")
    private String color;

    @JsonProperty("icon")
    @Schema(description = "Account icon identifier", example = "bank")
    private String icon;
}
