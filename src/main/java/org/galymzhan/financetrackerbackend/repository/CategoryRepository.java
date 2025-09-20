package org.galymzhan.financetrackerbackend.repository;

import org.galymzhan.financetrackerbackend.entity.Category;
import org.galymzhan.financetrackerbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByUser(User user);

    Optional<Category> findByIdAndUser(Long id, User user);

    List<Category> findAllByIdInAndUser(Set<Long> categoryIds, User user);
}
