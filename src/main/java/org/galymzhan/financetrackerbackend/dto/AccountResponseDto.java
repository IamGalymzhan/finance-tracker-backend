package org.galymzhan.financetrackerbackend.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class AccountResponseDto {
    private String id;

    private String name;

    private String accountType;

    private BigDecimal balance;

    private String color;

    private String icon;
}
