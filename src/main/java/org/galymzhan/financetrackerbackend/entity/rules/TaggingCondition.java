package org.galymzhan.financetrackerbackend.entity.rules;

import jakarta.persistence.*;
import lombok.*;
import org.galymzhan.financetrackerbackend.entity.base.BaseEntityAudit;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tagging_conditions")
public class TaggingCondition extends BaseEntityAudit {

    @ManyToOne(optional = false)
    @JoinColumn(name = "rule_id")
    private TaggingRule rule;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConditionField field;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConditionOperator operator;

    @Column(nullable = false)
    private String value;
}