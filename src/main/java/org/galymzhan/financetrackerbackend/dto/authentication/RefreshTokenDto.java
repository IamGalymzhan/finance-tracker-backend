package org.galymzhan.financetrackerbackend.dto.authentication;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RefreshTokenDto {

    @NotBlank(message = "Refresh token is required")
    @JsonProperty("refreshToken")
    String refreshToken;
} 