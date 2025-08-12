package org.galymzhan.financetrackerbackend.entity.rules;

import jakarta.persistence.*;
import lombok.*;
import org.galymzhan.financetrackerbackend.entity.Tag;
import org.galymzhan.financetrackerbackend.entity.User;
import org.galymzhan.financetrackerbackend.entity.base.BaseEntityAudit;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tagging_rules")
public class TaggingRule extends BaseEntityAudit {

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    private String name;

    @Enumerated(EnumType.STRING)
    private LogicalOperator logicalOperator;

    @OneToMany(mappedBy = "rule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaggingCondition> conditions;

    @ManyToMany
    @JoinTable(
            name = "tagging_rule_tags",
            joinColumns = @JoinColumn(name = "rule_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tagsToApply = new HashSet<>();

    private boolean active;
}