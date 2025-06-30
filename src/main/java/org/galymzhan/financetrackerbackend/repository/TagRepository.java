package org.galymzhan.financetrackerbackend.repository;

import org.galymzhan.financetrackerbackend.entity.Tag;
import org.galymzhan.financetrackerbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findAllByUser(User user);

    List<Tag> findAllByIdInAndUser(Set<Long> ids, User user);

    Optional<Tag> findByIdAndUser(Long id, User user);
}
