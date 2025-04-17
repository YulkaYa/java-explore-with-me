package ru.practicum.event.dal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.category.model.Category;
import ru.practicum.event.model.EventState;
import ru.practicum.event.model.Event;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.category AND LEFT JOIN FETCH e.initiator  WHERE e.initiator.id in :userId")
    Page<Event> findByInitiatorId(Long userId, Pageable page);

    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.category AND LEFT JOIN FETCH e.initiator  WHERE e.id IN :eventIds")
    List<Event> findAllWithCategoriesByEventIds(@Param("eventIds") List<Long> eventIds);

    default Map<Long, Category> getCategoriesMapByEventIds(List<Long> eventIds) {
        return findAllWithCategoriesByEventIds(eventIds).stream()
                .collect(Collectors.toMap(
                        Event::getId,
                        Event::getCategory
                ));
    }

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    @Query("SELECT e FROM Event e " +
            "LEFT JOIN FETCH e.category AND LEFT JOIN FETCH e.initiator " +
            "WHERE (:users IS NULL OR e.initiator.id IN :users) " +
            "AND (:states IS NULL OR e.state IN :states) " +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND (CAST(:rangeStart AS TIMESTAMP) IS NULL OR e.eventDate >= :rangeStart) " +
            "AND (CAST(:rangeEnd AS TIMESTAMP) IS NULL OR e.eventDate <= :rangeEnd)")
    Page<Event> findEventsByFilters(
            @Param("users") List<Long> users,
            @Param("states") List<EventState> states,
            @Param("categories") List<Long> categories,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            Pageable pageable);


    @Query("SELECT e FROM Event e " +
            "LEFT JOIN FETCH e.category AND LEFT JOIN FETCH e.initiator " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND (LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) OR :text IS NULL) " +
            "AND (e.category.id IN :categories OR :categories IS NULL) " +
            "AND (e.paid = :paid OR :paid IS NULL) " +
            "AND (e.eventDate >= :rangeStart OR CAST(:rangeStart AS TIMESTAMP)  IS NULL) " +
            "AND (e.eventDate <= :rangeEnd OR CAST(:rangeEnd AS TIMESTAMP) IS NULL) ")
    Page<Event> findPublishedEvents(
            @Param("text") String text,
            @Param("categories") List<Long> categories,
            @Param("paid") Boolean paid,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            Pageable page);

    @Query("SELECT e.id FROM Event e WHERE e.category.id in :categoryId")
    Page<Long> findIdsByCategoryId(Long categoryId, Pageable page);

    @Query("SELECT e.id FROM Event e WHERE e.id in :eventId")
    List<Long> findIdsByEventId(List<Long> eventId);
}