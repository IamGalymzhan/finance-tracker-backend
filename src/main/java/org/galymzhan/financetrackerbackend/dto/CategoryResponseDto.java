package org.galymzhan.financetrackerbackend.dto;

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
public class CategoryResponseDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("parentCategoryId")
    private Long parentCategoryId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("direction")
    private String direction;

    @JsonProperty("targetAmount")
    private BigDecimal targetAmount;

    @JsonProperty("color")
    private String color;

    @JsonProperty("icon")
    private String icon;
}
