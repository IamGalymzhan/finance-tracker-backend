package org.galymzhan.financetrackerbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class CategoryResponseDto {

    @JsonProperty("id")
    Long id;

    @JsonProperty("parentCategoryId")
    Long parentCategoryId;

    @JsonProperty("name")
    String name;

    @JsonProperty("description")
    String description;

    @JsonProperty("direction")
    String direction;

    @JsonProperty("targetAmount")
    BigDecimal targetAmount;

    @JsonProperty("color")
    String color;

    @JsonProperty("icon")
    String icon;
}
