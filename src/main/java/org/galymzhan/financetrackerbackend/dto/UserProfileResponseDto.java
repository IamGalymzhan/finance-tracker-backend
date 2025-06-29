package org.galymzhan.financetrackerbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserProfileResponseDto {

    @JsonProperty("id")
    Long id;

    @JsonProperty("username")
    String username;

    @JsonProperty("email")
    String email;
}
