package org.galymzhan.financetrackerbackend.dto.authentication;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class LoginDto {

    @NotBlank(message = "Username is required")
    @JsonProperty("username")
    String username;

    @NotBlank(message = "Password is required")
    @JsonProperty("password")
    String password;

}