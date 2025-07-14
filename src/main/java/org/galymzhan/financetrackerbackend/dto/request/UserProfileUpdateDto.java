package org.galymzhan.financetrackerbackend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileUpdateDto {

    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @JsonProperty("username")
    private String username;

    @Email(message = "Email should be valid")
    @JsonProperty("email")
    private String email;
}
