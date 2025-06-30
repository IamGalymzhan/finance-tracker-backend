package org.galymzhan.financetrackerbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class CategoryRequestDto {

    @JsonProperty("parentCategoryId")
    Long parentCategoryId;

    @JsonProperty("name")
    String name;

    @JsonProperty("description")
    String description;

    @JsonProperty("direction")
    String direction;

    @Positive
    @JsonProperty("targetAmount")
    BigDecimal targetAmount;

    @JsonProperty("color")
    String color;

    @JsonProperty("icon")
    String icon;
}
