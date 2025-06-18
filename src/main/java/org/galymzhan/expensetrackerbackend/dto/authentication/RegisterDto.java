package org.galymzhan.expensetrackerbackend.dto.authentication;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RegisterDto {

    @JsonProperty("username")
    private String username;

    @JsonProperty("password")
    private String password;

}