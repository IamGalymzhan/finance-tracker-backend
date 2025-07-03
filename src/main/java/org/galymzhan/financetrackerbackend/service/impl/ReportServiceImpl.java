package org.galymzhan.financetrackerbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.galymzhan.financetrackerbackend.dto.ReportCategoryDto;
import org.galymzhan.financetrackerbackend.dto.ReportOverviewDto;
import org.galymzhan.financetrackerbackend.entity.Category;
import org.galymzhan.financetrackerbackend.entity.Operation;
import org.galymzhan.financetrackerbackend.entity.OperationType;
import org.galymzhan.financetrackerbackend.entity.User;
import org.galymzhan.financetrackerbackend.repository.OperationRepository;
import org.galymzhan.financetrackerbackend.service.AuthenticationService;
import org.galymzhan.financetrackerbackend.service.ReportService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final OperationRepository operationRepository;

    private final AuthenticationService authenticationService;

    @Override
    public ReportOverviewDto getReportOverview(LocalDate startDate, LocalDate endDate) {
        User user = authenticationService.getCurrentUser();
        List<Operation> operations = operationRepository.findAllByUserAndDateBetween(user, startDate, endDate);

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        Map<Category, BigDecimal> categoryAmountMap = new HashMap<>();

        for (var operation : operations) {
            BigDecimal amount = operation.getAmount();
            if (operation.getOperationType() == OperationType.INCOME) {
                totalIncome = totalIncome.add(amount);
            } else if (operation.getOperationType() == OperationType.EXPENSE) {
                totalExpense = totalExpense.add(amount);
            }

            categoryAmountMap.merge(operation.getCategory(), amount, BigDecimal::add);
        }

        List<ReportCategoryDto> byCategory = categoryAmountMap.entrySet().stream()
                .map(entry -> {
                    Category category = entry.getKey();
                    BigDecimal amount = entry.getValue();
                    return ReportCategoryDto.builder()
                            .categoryId(category.getId())
                            .categoryName(category.getName())
                            .amount(amount)
                            .direction(category.getDirection())
                            .color(category.getColor())
                            .build();
                }).toList();

        return ReportOverviewDto.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .net(totalIncome.subtract(totalExpense))
                .byCategory(byCategory)
                .build();
    }
}
