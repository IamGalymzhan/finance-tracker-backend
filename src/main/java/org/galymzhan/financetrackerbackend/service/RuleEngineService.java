package org.galymzhan.financetrackerbackend.service;

import org.galymzhan.financetrackerbackend.entity.Operation;
import org.galymzhan.financetrackerbackend.entity.Tag;
import org.galymzhan.financetrackerbackend.entity.rules.TaggingRule;

import java.util.List;
import java.util.Set;

public interface RuleEngineService {
    Set<Tag> evaluateRules(Operation operation, List<TaggingRule> rules);
}
