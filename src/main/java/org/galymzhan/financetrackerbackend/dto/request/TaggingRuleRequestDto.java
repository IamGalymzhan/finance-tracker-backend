package org.galymzhan.financetrackerbackend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.galymzhan.financetrackerbackend.entity.rules.LogicalOperator;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Tagging rule creation/update request")
public class TaggingRuleRequestDto {

    @Size(min = 1, max = 100, message = "Rule name must be between 1 and 100 characters")
    @Schema(description = "Rule name", example = "Uber rides", maxLength = 100)
    private String name;

    @Schema(description = "Logical operator for combining conditions", example = "AND")
    private LogicalOperator logicalOperator;

    @Valid
    @Size(min = 1, message = "At least one condition is required")
    @Schema(description = "List of conditions for the rule")
    private List<TaggingConditionRequestDto> conditions;

    @Size(min = 1, message = "At least one tag must be applied")
    @Schema(description = "IDs of tags to apply when rule matches")
    private Set<Long> tagIds;

    @Schema(description = "Whether the rule is active", example = "true", defaultValue = "true")
    private Boolean active = true;
}
