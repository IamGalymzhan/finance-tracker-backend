package org.galymzhan.expensetrackerbackend.dto.authentication;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AuthenticationDto {

    @JsonProperty("token")
    private String token;

    @JsonProperty("role")
    private String role;

}