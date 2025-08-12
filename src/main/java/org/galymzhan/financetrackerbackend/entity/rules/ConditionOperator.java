package org.galymzhan.financetrackerbackend.entity.rules;

public enum ConditionOperator {
    EQUALS,
    CONTAINS,
    STARTS_WITH,
    ENDS_WITH,
    GREATER_THAN,
    LESS_THAN,
    GREATER_THAN_OR_EQUAL,
    LESS_THAN_OR_EQUAL,
    BETWEEN,
    NOT_EQUALS,
    NOT_CONTAINS,
    REGEX_MATCHES,
    IN,
    NOT_IN,
    IS_NULL,
    IS_NOT_NULL,
}
