package org.galymzhan.financetrackerbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.galymzhan.financetrackerbackend.entity.Direction;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportCategoryDto {

    private Long categoryId;

    private String categoryName;

    private BigDecimal amount;

    private Direction direction;

    private String color;
}
