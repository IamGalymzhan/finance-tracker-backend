package org.galymzhan.expensetrackerbackend.dto.authentication;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoginDto {

    @JsonProperty("username")
    private String username;

    @JsonProperty("password")
    private String password;

}