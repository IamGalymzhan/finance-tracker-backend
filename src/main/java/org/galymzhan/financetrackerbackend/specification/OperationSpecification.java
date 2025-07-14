package org.galymzhan.financetrackerbackend.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.galymzhan.financetrackerbackend.dto.filter.OperationFilterDto;
import org.galymzhan.financetrackerbackend.entity.Operation;
import org.galymzhan.financetrackerbackend.entity.Tag;
import org.galymzhan.financetrackerbackend.entity.User;
import org.springframework.data.jpa.domain.Specification;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OperationSpecification {

    public static Specification<Operation> withFilters(OperationFilterDto filters, User user) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("user"), user));

            if (filters.getOperationType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("operationType"), filters.getOperationType()));
            }

            if (filters.getCategoryIds() != null && !filters.getCategoryIds().isEmpty()) {
                predicates.add(root.get("category").get("id").in(filters.getCategoryIds()));
            }

            if (filters.getAccountIds() != null && !filters.getAccountIds().isEmpty()) {
                Predicate accountInPredicate = root.get("accountIn").get("id").in(filters.getAccountIds());
                Predicate accountOutPredicate = root.get("accountOut").get("id").in(filters.getAccountIds());
                predicates.add(criteriaBuilder.or(accountInPredicate, accountOutPredicate));
            }

            if (filters.getTagIds() != null && !filters.getTagIds().isEmpty()) {
                Join<Operation, Tag> tagJoin = root.join("tags", JoinType.INNER);
                predicates.add(tagJoin.get("id").in(filters.getTagIds()));
            }

            if (filters.getMinAmount() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("amount"), filters.getMinAmount()));
            }
            if (filters.getMaxAmount() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("amount"), filters.getMaxAmount()));
            }

            if (filters.getStartDate() != null) {
                Date startDate = Date.from(filters.getStartDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("date"), startDate));
            }
            if (filters.getEndDate() != null) {
                Date endDate = Date.from(filters.getEndDate().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
                predicates.add(criteriaBuilder.lessThan(root.get("date"), endDate));
            }

            if (filters.getSearchText() != null && !filters.getSearchText().trim().isEmpty()) {
                String searchPattern = "%" + filters.getSearchText().toLowerCase() + "%";
                Predicate namePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")), searchPattern);
                Predicate notePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("note")), searchPattern);
                predicates.add(criteriaBuilder.or(namePredicate, notePredicate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}