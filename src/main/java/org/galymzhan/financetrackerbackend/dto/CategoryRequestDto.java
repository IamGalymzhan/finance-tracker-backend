package org.galymzhan.financetrackerbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class CategoryRequestDto {

    @JsonProperty("parentCategoryId")
    Long parentCategoryId;

    @Size(min = 1, max = 100)
    @JsonProperty("name")
    String name;

    @Size(min = 1, max = 200)
    @JsonProperty("description")
    String description;

    @JsonProperty("direction")
    String direction;

    @PositiveOrZero
    @JsonProperty("targetAmount")
    BigDecimal targetAmount;

    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$")
    @JsonProperty("color")
    String color;

    @JsonProperty("icon")
    String icon;
}
