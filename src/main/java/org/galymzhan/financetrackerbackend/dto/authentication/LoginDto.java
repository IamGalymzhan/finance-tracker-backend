package org.galymzhan.financetrackerbackend.dto.authentication;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@Schema(description = "User login request")
public class LoginDto {

    @NotBlank(message = "Username is required")
    @JsonProperty("username")
    @Schema(description = "Username or email", example = "john_doe")
    String username;

    @NotBlank(message = "Password is required")
    @JsonProperty("password")
    @Schema(description = "Account password", example = "securePassword123")
    String password;

}