package ru.practicum.participation.service;

import ru.practicum.participation.dto.EventRequestStatusUpdateRequest;
import ru.practicum.participation.dto.EventRequestStatusUpdateResult;
import ru.practicum.participation.dto.ParticipationRequestDto;

import java.util.List;

public interface ParticipationRequestService {
    // Создание заявки на участие
    ParticipationRequestDto addParticipationRequest(Long userId, Long eventId);

    // Отмена заявки на участие
    ParticipationRequestDto cancelParticipationRequest(Long userId, Long requestId);

    // Получение заявок на участие в событии текущего пользователя
    List<ParticipationRequestDto> getParticipationsByRequesterId(Long userId);

    EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequest);

    List<ParticipationRequestDto> getEventParticipants(Long userId, Long eventId);
}
