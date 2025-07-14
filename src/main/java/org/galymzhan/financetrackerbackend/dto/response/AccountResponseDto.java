package org.galymzhan.financetrackerbackend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponseDto {

    @JsonProperty("id")
    private Long id;

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
