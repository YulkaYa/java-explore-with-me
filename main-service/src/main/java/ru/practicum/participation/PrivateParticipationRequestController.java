package ru.practicum.participation;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.participation.dto.ParticipationRequestDto;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PrivateParticipationRequestController {
    private final ParticipationRequestService participationRequestService;

    // Подача заявки на участие в мероприятии
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addParticipationRequest(@PathVariable Long userId, @RequestParam(required = true) Long eventId) {
        return participationRequestService.addParticipationRequest(userId, eventId);
    }

    // Отмена заявки на участие
    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancelParticipationRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        return participationRequestService.cancelParticipationRequest(userId, requestId);
    }

    // Получение заявок на участие в мероприятиях текущего пользователя
    @GetMapping
    public List<ParticipationRequestDto> getUserRequests(@PathVariable Long userId) {
        return participationRequestService.getEventParticipants(userId, null);
    }
}