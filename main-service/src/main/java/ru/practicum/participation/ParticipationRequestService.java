package ru.practicum.participation;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.participation.dto.EventRequestStatusUpdateRequest;
import ru.practicum.participation.dto.EventRequestStatusUpdateResult;
import ru.practicum.participation.dto.ParticipationRequestDto;

import java.util.List;

public interface ParticipationRequestService {
    // Создание заявки на участие
    @Transactional
    ParticipationRequestDto addParticipationRequest(Long userId, Long eventId);

    // Отмена заявки на участие
    @Transactional
    ParticipationRequestDto cancelParticipationRequest(Long userId, Long requestId);

    // Получение заявок на участие в событии текущего пользователя
    List<ParticipationRequestDto> getEventParticipants(Long userId, Long eventId);

   @Transactional
   EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequest);
}
