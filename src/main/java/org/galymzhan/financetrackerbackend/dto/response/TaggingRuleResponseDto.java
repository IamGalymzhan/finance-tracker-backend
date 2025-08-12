package org.galymzhan.financetrackerbackend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.galymzhan.financetrackerbackend.entity.rules.LogicalOperator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Tagging rule response")
public class TaggingRuleResponseDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("logicalOperator")
    private LogicalOperator logicalOperator;

    @JsonProperty("conditions")
    private List<TaggingConditionResponseDto> conditions;

    @JsonProperty("tagsToApply")
    private Set<TagResponseDto> tagsToApply;

    @JsonProperty("active")
    private Boolean active;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;
}
