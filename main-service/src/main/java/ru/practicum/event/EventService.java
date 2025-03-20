package ru.practicum.event;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.UpdateEventUserRequest;

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
    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);
}
