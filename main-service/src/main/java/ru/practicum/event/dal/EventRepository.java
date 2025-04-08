package ru.practicum.event.dal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Range;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.EventState;
import ru.practicum.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.category AND LEFT JOIN FETCH e.initiator  WHERE e.initiator.id in :userId")
    Page<Event> findByInitiatorId(Long userId, Pageable page); //todo убрать джойны и оставить ручную выгрузку категорий?

    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.category AND LEFT JOIN FETCH e.initiator  WHERE e.id IN :eventIds")
    List<Event> findAllWithCategoriesByEventIds(@Param("eventIds") List<Long> eventIds); // todo поправить название и использовать везде для получения полного event?

    default Map<Long, Category> getCategoriesMapByEventIds(List<Long> eventIds) {
        return findAllWithCategoriesByEventIds(eventIds).stream()
                .collect(Collectors.toMap(
                        Event::getId,
                        Event::getCategory
                ));
    }

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);// todo проверить выборку , что нет лишних запросов

    // todo todotodotodo
    @Query("SELECT e FROM Event e " +
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
            Pageable pageable); // todo проверить выборку , что нет лишних запросов


    @Query("SELECT e FROM Event e " +
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
            Pageable page); // todo проверить выборку , что нет лишних запросов - запросы есть в общем методе загрузки в стриме с map

    @Query("SELECT e.id FROM Event e WHERE e.category.id in :categoryId")
    Page<Long> findIdsByCategoryId(Long categoryId, Pageable page); // todo проверено что нет запросов

    @Query("SELECT e.id FROM Event e WHERE e.id in :eventId")
    List<Long> findIdsByEventId(List<Long> eventId); // todo проверено что нет запросов
}