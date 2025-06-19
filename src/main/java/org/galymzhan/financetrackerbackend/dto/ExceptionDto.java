package org.galymzhan.financetrackerbackend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExceptionDto {

    @JsonProperty("error_code")
    private String errorCode;

    @JsonProperty("message")
    private String message;

}
