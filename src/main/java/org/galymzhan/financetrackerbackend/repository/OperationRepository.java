package org.galymzhan.financetrackerbackend.repository;

import org.galymzhan.financetrackerbackend.entity.Operation;
import org.galymzhan.financetrackerbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OperationRepository extends JpaRepository<Operation, Long> {
    List<Operation> findAllByUser(User user);

    Optional<Operation> findByIdAndUser(Long id, User user);
}
