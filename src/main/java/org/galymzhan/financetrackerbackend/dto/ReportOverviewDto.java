package org.galymzhan.financetrackerbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportOverviewDto {

    private BigDecimal totalIncome;

    private BigDecimal totalExpense;

    private BigDecimal net;

    private List<ReportCategoryDto> byCategory;
}
