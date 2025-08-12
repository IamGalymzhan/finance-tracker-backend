package org.galymzhan.financetrackerbackend.repository;

import org.galymzhan.financetrackerbackend.entity.Operation;
import org.galymzhan.financetrackerbackend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OperationRepository extends JpaRepository<Operation, Long>, JpaSpecificationExecutor<Operation> {
    List<Operation> findAllByUser(User user);

    Page<Operation> findAllByUser(User user, Pageable pageable);

    List<Operation> findAllByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);

    Optional<Operation> findByIdAndUser(Long id, User user);

    List<Operation> findAllByIdInAndUser(List<Long> operationIds, User user);
}
