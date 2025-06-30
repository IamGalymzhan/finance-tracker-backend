package org.galymzhan.financetrackerbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TagRequestDto {

    @JsonProperty("name")
    String name;

    @JsonProperty("color")
    String color;
}
