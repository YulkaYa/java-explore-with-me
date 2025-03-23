package ru.practicum.event;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.BaseUpdateEventRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    // Получение событий, добавленных текущим пользователем
    List<EventShortDto> getEventsByUser(Long userId, int from, int size);

    // Добавление нового события
    @Transactional
    EventFullDto addEvent(Long userId, NewEventDto newEventDto);

    // Получение события по ID и пользователю
    EventFullDto getEventByIdAndUser(Long userId, Long eventId);

    // Изменение события
    @Transactional
    <T extends BaseUpdateEventRequest> EventFullDto updateEvent(Long userId, Long eventId, T updateEventRequest, long durationHours);

    List<EventFullDto> getEvents(List<Long> users, List<EventState> states, List<Long> categories,
                                  LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);

    List<EventShortDto> getPublishedEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, int from, int size);
}
