package org.galymzhan.financetrackerbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TagRequestDto {

    @Size(min = 1, max = 100)
    @JsonProperty("name")
    String name;

    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "Color must be a valid hex color code")
    @JsonProperty("color")
    String color;
}
