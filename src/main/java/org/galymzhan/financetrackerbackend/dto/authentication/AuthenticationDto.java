package org.galymzhan.financetrackerbackend.dto.authentication;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AuthenticationDto {

    @JsonProperty("token")
    String token;

    @JsonProperty("role")
    String role;
}