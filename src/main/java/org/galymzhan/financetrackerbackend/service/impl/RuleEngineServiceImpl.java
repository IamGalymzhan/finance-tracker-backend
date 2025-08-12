package org.galymzhan.financetrackerbackend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.galymzhan.financetrackerbackend.entity.Account;
import org.galymzhan.financetrackerbackend.entity.Category;
import org.galymzhan.financetrackerbackend.entity.Operation;
import org.galymzhan.financetrackerbackend.entity.Tag;
import org.galymzhan.financetrackerbackend.entity.enums.AccountType;
import org.galymzhan.financetrackerbackend.entity.enums.Direction;
import org.galymzhan.financetrackerbackend.entity.rules.ConditionOperator;
import org.galymzhan.financetrackerbackend.entity.rules.LogicalOperator;
import org.galymzhan.financetrackerbackend.entity.rules.TaggingCondition;
import org.galymzhan.financetrackerbackend.entity.rules.TaggingRule;
import org.galymzhan.financetrackerbackend.service.RuleEngineService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class RuleEngineServiceImpl implements RuleEngineService {

    @Override
    public Set<Tag> evaluateRules(Operation operation, List<TaggingRule> rules) {
        Set<Tag> tags = new HashSet<>();

        for (TaggingRule rule : rules) {
            if (!rule.isActive()) continue;

            try {
                boolean matches = evaluateRule(rule, operation);
                if (matches) {
                    tags.addAll(rule.getTagsToApply());
                }
            } catch (Exception e) {
                log.warn("Failed to evaluate rule {}: {}", rule.getId(), e.getMessage());
            }
        }

        return tags;
    }

    private boolean evaluateRule(TaggingRule rule, Operation operation) {
        if (rule.getConditions() == null || rule.getConditions().isEmpty()) {
            return false;
        }

        if (rule.getLogicalOperator() == LogicalOperator.AND) {
            return rule.getConditions().stream()
                    .allMatch(condition -> evaluateCondition(condition, operation));
        } else {
            return rule.getConditions().stream()
                    .anyMatch(condition -> evaluateCondition(condition, operation));
        }
    }

    private boolean evaluateCondition(TaggingCondition condition, Operation operation) {
        String rawValue = condition.getValue();

        return switch (condition.getField()) {
            case NAME -> evaluateString(operation.getName(), condition.getOperator(), rawValue);
            case NOTE -> evaluateString(operation.getNote(), condition.getOperator(), rawValue);
            case OPERATION_TYPE -> evaluateEnum(operation.getOperationType().name(), condition.getOperator(), rawValue);
            case AMOUNT -> evaluateBigDecimal(operation.getAmount(), condition.getOperator(), rawValue);
            case DATE -> evaluateDate(operation.getDate(), condition.getOperator(), rawValue);
            case CATEGORY_ID -> evaluateLong(getCategoryId(operation), condition.getOperator(), rawValue);
            case CATEGORY_NAME -> evaluateString(getCategoryName(operation), condition.getOperator(), rawValue);
            case PARENT_CATEGORY_ID -> evaluateLong(getParentCategoryId(operation), condition.getOperator(), rawValue);
            case TOP_LEVEL_CATEGORY_ID -> evaluateLong(getTopLevelCategoryId(operation), condition.getOperator(), rawValue);
            case CATEGORY_DIRECTION -> evaluateEnum(getCategoryDirection(operation), condition.getOperator(), rawValue);
            case ACCOUNT_IN_ID -> evaluateLong(getAccountInId(operation), condition.getOperator(), rawValue);
            case ACCOUNT_OUT_ID -> evaluateLong(getAccountOutId(operation), condition.getOperator(), rawValue);
            case ACCOUNT_IN_TYPE -> evaluateEnum(getAccountInType(operation), condition.getOperator(), rawValue);
            case ACCOUNT_OUT_TYPE -> evaluateEnum(getAccountOutType(operation), condition.getOperator(), rawValue);
            case DAY_OF_WEEK -> evaluateInteger(operation.getDate().getDayOfWeek().getValue(), condition.getOperator(), rawValue);
            case MONTH -> evaluateInteger(operation.getDate().getMonthValue(), condition.getOperator(), rawValue);
            case YEAR -> evaluateInteger(operation.getDate().getYear(), condition.getOperator(), rawValue);
            case CREATED_AT -> evaluateDateTime(operation.getCreatedAt(), condition.getOperator(), rawValue);
            case UPDATED_AT -> evaluateDateTime(operation.getUpdatedAt(), condition.getOperator(), rawValue);
        };
    }

    private boolean evaluateString(String fieldValue, ConditionOperator op, String value) {
        return switch (op) {
            case EQUALS -> fieldValue != null && fieldValue.equalsIgnoreCase(value);
            case NOT_EQUALS -> fieldValue == null || !fieldValue.equalsIgnoreCase(value);
            case CONTAINS -> fieldValue != null && value != null && fieldValue.toLowerCase().contains(value.toLowerCase());
            case NOT_CONTAINS -> fieldValue == null || value == null || !fieldValue.toLowerCase().contains(value.toLowerCase());
            case STARTS_WITH -> fieldValue != null && value != null && fieldValue.toLowerCase().startsWith(value.toLowerCase());
            case ENDS_WITH -> fieldValue != null && value != null && fieldValue.toLowerCase().endsWith(value.toLowerCase());
            case REGEX_MATCHES -> fieldValue != null && value != null && fieldValue.matches(value);
            case IS_NULL -> fieldValue == null;
            case IS_NOT_NULL -> fieldValue != null;
            case IN -> fieldValue != null && value != null &&
                    Arrays.stream(value.split(","))
                            .map(String::trim).map(String::toLowerCase)
                            .anyMatch(v -> fieldValue.toLowerCase().equals(v));
            case NOT_IN -> fieldValue == null || value == null ||
                    Arrays.stream(value.split(","))
                            .map(String::trim).map(String::toLowerCase)
                            .noneMatch(v -> fieldValue.toLowerCase().equals(v));
            default -> false;
        };
    }

    private boolean evaluateEnum(String fieldValue, ConditionOperator op, String value) {
        return switch (op) {
            case EQUALS -> Objects.equals(fieldValue, value);
            case NOT_EQUALS -> !Objects.equals(fieldValue, value);
            case IN -> fieldValue != null && Arrays.asList(value.split(",")).contains(fieldValue.trim());
            case NOT_IN -> fieldValue == null || !Arrays.asList(value.split(",")).contains(fieldValue.trim());
            case IS_NULL -> fieldValue == null;
            case IS_NOT_NULL -> fieldValue != null;
            default -> false;
        };
    }

    private boolean evaluateBigDecimal(BigDecimal fieldValue, ConditionOperator op, String value) {
        if (fieldValue == null && (op == ConditionOperator.IS_NULL)) {
            return true;
        }
        if (fieldValue == null) {
            return false;
        }
        if (op == ConditionOperator.IS_NOT_NULL) {
            return true;
        }

        try {
            return switch (op) {
                case EQUALS -> fieldValue.compareTo(new BigDecimal(value)) == 0;
                case NOT_EQUALS -> fieldValue.compareTo(new BigDecimal(value)) != 0;
                case GREATER_THAN -> fieldValue.compareTo(new BigDecimal(value)) > 0;
                case GREATER_THAN_OR_EQUAL -> fieldValue.compareTo(new BigDecimal(value)) >= 0;
                case LESS_THAN -> fieldValue.compareTo(new BigDecimal(value)) < 0;
                case LESS_THAN_OR_EQUAL -> fieldValue.compareTo(new BigDecimal(value)) <= 0;
                case BETWEEN -> evaluateBetween(fieldValue, value, BigDecimal::new, BigDecimal::compareTo);
                case IN -> evaluateInList(fieldValue, value, BigDecimal::new, BigDecimal::equals);
                case NOT_IN -> !evaluateInList(fieldValue, value, BigDecimal::new, BigDecimal::equals);
                default -> false;
            };
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean evaluateInteger(Integer fieldValue, ConditionOperator op, String value) {
        if (fieldValue == null && (op == ConditionOperator.IS_NULL)) {
            return true;
        }
        if (fieldValue == null) {
            return false;
        }
        if (op == ConditionOperator.IS_NOT_NULL) {
            return true;
        }

        try {
            return switch (op) {
                case EQUALS -> fieldValue.equals(Integer.valueOf(value));
                case NOT_EQUALS -> !fieldValue.equals(Integer.valueOf(value));
                case GREATER_THAN -> fieldValue > Integer.parseInt(value);
                case GREATER_THAN_OR_EQUAL -> fieldValue >= Integer.parseInt(value);
                case LESS_THAN -> fieldValue < Integer.parseInt(value);
                case LESS_THAN_OR_EQUAL -> fieldValue <= Integer.parseInt(value);
                case BETWEEN -> evaluateBetween(fieldValue, value, Integer::valueOf, Integer::compareTo);
                case IN -> evaluateInList(fieldValue, value, Integer::valueOf, Integer::equals);
                case NOT_IN -> !evaluateInList(fieldValue, value, Integer::valueOf, Integer::equals);
                default -> false;
            };
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean evaluateLong(Long fieldValue, ConditionOperator op, String value) {
        if (fieldValue == null && (op == ConditionOperator.IS_NULL)) {
            return true;
        }
        if (fieldValue == null) {
            return false;
        }
        if (op == ConditionOperator.IS_NOT_NULL) {
            return true;
        }

        try {
            return switch (op) {
                case EQUALS -> fieldValue.equals(Long.valueOf(value));
                case NOT_EQUALS -> !fieldValue.equals(Long.valueOf(value));
                case GREATER_THAN -> fieldValue > Long.parseLong(value);
                case GREATER_THAN_OR_EQUAL -> fieldValue >= Long.parseLong(value);
                case LESS_THAN -> fieldValue < Long.parseLong(value);
                case LESS_THAN_OR_EQUAL -> fieldValue <= Long.parseLong(value);
                case BETWEEN -> evaluateBetween(fieldValue, value, Long::valueOf, Long::compareTo);
                case IN -> evaluateInList(fieldValue, value, Long::valueOf, Long::equals);
                case NOT_IN -> !evaluateInList(fieldValue, value, Long::valueOf, Long::equals);
                default -> false;
            };
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean evaluateDate(LocalDate fieldValue, ConditionOperator op, String value) {
        if (fieldValue == null && (op == ConditionOperator.IS_NULL)) {
            return true;
        }
        if (fieldValue == null) {
            return false;
        }
        if (op == ConditionOperator.IS_NOT_NULL) {
            return true;
        }

        try {
            return switch (op) {
                case EQUALS -> fieldValue.equals(LocalDate.parse(value));
                case NOT_EQUALS -> !fieldValue.equals(LocalDate.parse(value));
                case GREATER_THAN -> fieldValue.isAfter(LocalDate.parse(value));
                case GREATER_THAN_OR_EQUAL -> !fieldValue.isBefore(LocalDate.parse(value));
                case LESS_THAN -> fieldValue.isBefore(LocalDate.parse(value));
                case LESS_THAN_OR_EQUAL -> !fieldValue.isAfter(LocalDate.parse(value));
                case BETWEEN -> evaluateBetween(fieldValue, value, LocalDate::parse, LocalDate::compareTo);
                case IN -> evaluateInList(fieldValue, value, LocalDate::parse, LocalDate::equals);
                case NOT_IN -> !evaluateInList(fieldValue, value, LocalDate::parse, LocalDate::equals);
                default -> false;
            };
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private boolean evaluateDateTime(LocalDateTime fieldValue, ConditionOperator op, String value) {
        if (fieldValue == null && (op == ConditionOperator.IS_NULL)) {
            return true;
        }
        if (fieldValue == null) {
            return false;
        }
        if (op == ConditionOperator.IS_NOT_NULL) {
            return true;
        }

        try {
            return switch (op) {
                case EQUALS -> fieldValue.equals(LocalDateTime.parse(value));
                case NOT_EQUALS -> !fieldValue.equals(LocalDateTime.parse(value));
                case GREATER_THAN -> fieldValue.isAfter(LocalDateTime.parse(value));
                case GREATER_THAN_OR_EQUAL -> !fieldValue.isBefore(LocalDateTime.parse(value));
                case LESS_THAN -> fieldValue.isBefore(LocalDateTime.parse(value));
                case LESS_THAN_OR_EQUAL -> !fieldValue.isAfter(LocalDateTime.parse(value));
                case BETWEEN -> evaluateBetween(fieldValue, value, LocalDateTime::parse, LocalDateTime::compareTo);
                case IN -> evaluateInList(fieldValue, value, LocalDateTime::parse, LocalDateTime::equals);
                case NOT_IN -> !evaluateInList(fieldValue, value, LocalDateTime::parse, LocalDateTime::equals);
                default -> false;
            };
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private <T> boolean evaluateBetween(T fieldValue, String value, Function<String, T> parser, Comparator<T> comparator) {
        String[] range = value.split(",");
        if (range.length != 2) return false;
        try {
            T min = parser.apply(range[0].trim());
            T max = parser.apply(range[1].trim());
            return comparator.compare(fieldValue, min) >= 0 && comparator.compare(fieldValue, max) <= 0;
        } catch (Exception e) {
            return false;
        }
    }

    private <T> boolean evaluateInList(T fieldValue, String value, Function<String, T> parser,
                                       java.util.function.BiPredicate<T, T> comparator) {
        String[] values = value.split(",");
        try {
            for (String v : values) {
                T parsedValue = parser.apply(v.trim());
                if (comparator.test(fieldValue, parsedValue)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private Long getCategoryId(Operation operation) {
        return operation.getCategory() != null ? operation.getCategory().getId() : null;
    }

    private String getCategoryName(Operation operation) {
        return operation.getCategory() != null ? operation.getCategory().getName() : null;
    }

    private String getCategoryDirection(Operation operation) {
        Direction direction = operation.getCategory() != null ? operation.getCategory().getDirection() : null;
        return direction != null ? direction.name() : null;
    }

    private Long getParentCategoryId(Operation operation) {
        Category category = operation.getCategory();
        if (category == null || category.getParentCategory() == null) return null;
        return category.getParentCategory().getId();
    }

    private Long getTopLevelCategoryId(Operation operation) {
        Category category = operation.getCategory();
        if (category == null) return null;

        while (category.getParentCategory() != null) {
            category = category.getParentCategory();
        }
        return category.getId();
    }

    private Long getAccountInId(Operation operation) {
        return operation.getAccountIn() != null ? operation.getAccountIn().getId() : null;
    }

    private Long getAccountOutId(Operation operation) {
        return operation.getAccountOut() != null ? operation.getAccountOut().getId() : null;
    }

    private String getAccountInType(Operation operation) {
        Account account = operation.getAccountIn();
        if (account == null) return null;
        AccountType type = account.getAccountType();
        return type != null ? type.name() : null;
    }

    private String getAccountOutType(Operation operation) {
        Account account = operation.getAccountOut();
        if (account == null) return null;
        AccountType type = account.getAccountType();
        return type != null ? type.name() : null;
    }
}