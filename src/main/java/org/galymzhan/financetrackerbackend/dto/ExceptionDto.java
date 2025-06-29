package org.galymzhan.financetrackerbackend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.util.Map;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExceptionDto {

    @JsonProperty("errorCode")
    String errorCode;

    @JsonProperty("message")
    String message;

    @JsonProperty("timestamp")
    @Builder.Default
    String timestamp = java.time.Instant.now().toString();

    @JsonProperty("details")
    Map<String, Object> details;
}
