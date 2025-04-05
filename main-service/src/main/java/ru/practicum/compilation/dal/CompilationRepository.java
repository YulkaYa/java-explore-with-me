package ru.practicum.compilation.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.category.model.Category;
import ru.practicum.compilation.model.Compilation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    Page<Compilation> findByPinned(Boolean pinned, Pageable page); //todo убрать?

    @Query("SELECT c FROM Compilation c " +
            "WHERE c.pinned = :pinned OR :pinned IS NULL")
    Page<Compilation> findCompilationsByPinned(
            Boolean pinned,
            Pageable page);
}