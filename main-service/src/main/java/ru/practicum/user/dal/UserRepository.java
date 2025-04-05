package ru.practicum.user.dal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.user.model.User;

import java.util.Collection;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u " +
            "WHERE u.id in :ids OR :ids IS NULL")
    Page<User> findByIdIn(Collection<Long> ids, Pageable page);
    User findByEmail(String email);
}