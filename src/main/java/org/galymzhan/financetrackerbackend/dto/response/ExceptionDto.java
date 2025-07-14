package org.galymzhan.financetrackerbackend.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExceptionDto {

    @JsonProperty("errorCode")
    private String errorCode;

    @JsonProperty("message")
    private String message;

    @JsonProperty("timestamp")
    @Builder.Default
    private String timestamp = java.time.Instant.now().toString();

    @JsonProperty("details")
    private Map<String, Object> details;
}
