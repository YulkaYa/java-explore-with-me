package ru.practicum.compilation.dal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.compilation.model.Compilation;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    @Query("SELECT c FROM Compilation c " +
            "WHERE c.pinned = :pinned OR :pinned IS NULL")
    Page<Compilation> findCompilationsByPinned(
            Boolean pinned,
            Pageable page);
}