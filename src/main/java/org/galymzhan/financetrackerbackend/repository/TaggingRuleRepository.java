package org.galymzhan.financetrackerbackend.repository;

import org.galymzhan.financetrackerbackend.entity.User;
import org.galymzhan.financetrackerbackend.entity.rules.TaggingRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaggingRuleRepository extends JpaRepository<TaggingRule, Long> {

    List<TaggingRule> findAllByUser(User user);

    Optional<TaggingRule> findByIdAndUser(Long id, User user);

    List<TaggingRule> findAllByUserAndActiveTrue(User user);
}