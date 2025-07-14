package org.galymzhan.financetrackerbackend.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.galymzhan.financetrackerbackend.entity.enums.Direction;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportCategoryDto {

    private Long categoryId;

    private String categoryName;

    private BigDecimal amount;

    private Direction direction;

    private String color;
}
