package org.galymzhan.financetrackerbackend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.galymzhan.financetrackerbackend.entity.rules.ConditionField;
import org.galymzhan.financetrackerbackend.entity.rules.ConditionOperator;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Condition for tagging rule")
public class TaggingConditionRequestDto {

    @JsonProperty("field")
    private ConditionField field;

    @JsonProperty("operator")
    private ConditionOperator operator;

    @JsonProperty("value")
    private String value;
}