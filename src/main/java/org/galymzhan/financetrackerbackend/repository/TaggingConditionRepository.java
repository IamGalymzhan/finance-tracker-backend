package org.galymzhan.financetrackerbackend.repository;

import org.galymzhan.financetrackerbackend.entity.rules.TaggingCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaggingConditionRepository extends JpaRepository<TaggingCondition, Long> {

    void deleteByRuleId(Long ruleId);
}