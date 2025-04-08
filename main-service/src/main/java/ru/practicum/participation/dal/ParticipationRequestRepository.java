package ru.practicum.participation.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.category.model.Category;
import ru.practicum.event.model.Event;
import ru.practicum.participation.ParticipationRequestStatus;
import ru.practicum.participation.dto.RequestsCount;
import ru.practicum.participation.model.ParticipationRequest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    // Найти все заявки на участие для конкретного события
    List<ParticipationRequest> findByEventId(Long eventId);

    // Найти все заявки на участие для конкретного пользователя
    List<ParticipationRequest> findByRequesterId(Long requesterId);// todo проверить выгрузку, что нет доп запросов

    // Найти все заявки на участие для конкретного события и пользователя
    List<ParticipationRequest> findByEventIdAndRequesterId(Long eventId, Long requesterId);

    // Найти все заявки на участие для конкретного события с определенным статусом
    List<ParticipationRequest> findByEventIdAndStatus(Long eventId, ParticipationRequestStatus status);

    // Подсчитать количество подтвержденных заявок на участие для конкретного события
    @Query("SELECT COUNT(pr) FROM ParticipationRequest pr WHERE pr.event.id = :eventId AND pr.status = :status")
    long countByEventIdAndStatus(@Param("eventId") Long eventId, @Param("status") ParticipationRequestStatus status);

    // Найти все заявки на участие по списку ID
    @Query("SELECT pr FROM ParticipationRequest pr WHERE pr.id IN :requestIds")
    List<ParticipationRequest> findAllByIdIn(@Param("requestIds") List<Long> requestIds);

    // Проверить, существует ли заявка на участие для конкретного события и пользователя
    boolean existsByEventIdAndRequesterId(Long eventId, Long requesterId);

    @Query("SELECT new map(pr.event.id, COUNT(pr)) " +
            "FROM ParticipationRequest pr " +
            "WHERE pr.event.id IN :eventIds AND pr.status = :status " +
            "GROUP BY pr.event.id")
    List<Map<Long, Long>> countGroupedByEventIdAndStatus(
            @Param("eventIds") List<Long> eventIds,
            @Param("status") ParticipationRequestStatus status);

    default Map<Long, Long> countGroupedByEventIdAndStatusToMap(
            List<Long> eventIds,
            ParticipationRequestStatus status) {
        return countGroupedByEventIdAndStatus(eventIds, status).stream()
                .collect(Collectors.toMap(
                        map -> map.get("0"),
                        map -> map.get("1")
                ));
    }
}