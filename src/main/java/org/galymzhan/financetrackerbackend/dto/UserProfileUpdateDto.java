package org.galymzhan.financetrackerbackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserProfileUpdateDto {

    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    String username;

    @Email(message = "Email should be valid")
    String email;
}
