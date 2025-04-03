package ru.practicum.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.participation.ParticipationRequestService;
import ru.practicum.participation.dto.EventRequestStatusUpdateRequest;
import ru.practicum.participation.dto.EventRequestStatusUpdateResult;
import ru.practicum.participation.dto.ParticipationRequestDto;

import java.util.List;


@RestController
@RequestMapping("/users/{userId}/events")
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PrivateEventController {
    private final EventService eventService;
    private final ParticipationRequestService participationRequestService;

    // Получение событий, добавленных текущим пользователем
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEventsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        return eventService.getEventsByUser(userId, from, size);
    }

    // Добавление нового события
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated
    public EventFullDto addEvent(@PathVariable Long userId, @RequestBody @Valid NewEventDto newEventDto) {
        return eventService.addEvent(userId, newEventDto);
    }

    // Получение полной информации о событии, добавленном текущим пользователем
    @GetMapping("/{eventId}")
    public EventFullDto getEventById(
            @PathVariable (required = true) Long userId,
            @PathVariable (required = true) Long eventId) {
        return eventService.getEventByIdAndUser(userId, eventId);
    }

    // Изменение события, добавленного текущим пользователем
    @PatchMapping("/{eventId}")
    @Validated
    public EventFullDto updateEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) {
        return eventService.updateEvent(userId, eventId, updateEventUserRequest, 2);
    }

    // Получение информации о запросах на участие в событии текущего пользователя
    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getEventParticipants(
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        return participationRequestService.getEventParticipants(userId, eventId);
    }

    // Изменение статуса заявок на участие в событии текущего пользователя
    @PatchMapping("/{eventId}/requests")
    @Validated
    public EventRequestStatusUpdateResult updateRequestStatus(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody @Valid EventRequestStatusUpdateRequest updateRequest) {
        return participationRequestService.updateRequestStatus(userId, eventId, updateRequest);
    }
}












/* todo
    // Получение заявок на участие в конкретном событии текущего пользователя
    @GetMapping("/events/{eventId}")
    public List<ParticipationRequest> getEventParticipants(@PathVariable Long userId, @PathVariable Long eventId) {
        return participationRequestService.getEventParticipants(userId, eventId);
    }

    // Изменение статуса заявок на участие в событии
    @PatchMapping("/events/{eventId}")
    public EventRequestStatusUpdateResult updateRequestStatus(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody EventRequestStatusUpdateRequest updateRequest) {
        return participationRequestService.updateRequestStatus(userId, eventId, updateRequest);
    }


    // Изменение статуса заявок на участие в событии
    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new RuntimeException("Only the event initiator can update request status");
        }

        List<ParticipationRequest> requests = participationRequestRepository.findAllByIdIn(updateRequest.getRequestIds());

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();

        for (ParticipationRequest request : requests) {
            if (!request.getStatus().equals(ParticipationRequestStatus.PENDING)) {
                throw new RuntimeException("Request must be in PENDING state");
            }

            if (updateRequest.getStatus().equals(ParticipationRequestStatus.CONFIRMED)) {
                if (event.getParticipantLimit() > 0 && event.getParticipantLimit() <= participationRequestRepository.countByEventIdAndStatus(eventId, ParticipationRequestStatus.CONFIRMED)) {
                    request.setStatus(ParticipationRequestStatus.REJECTED);
                    result.getRejectedRequests().add(request);
                } else {
                    request.setStatus(ParticipationRequestStatus.CONFIRMED);
                    result.getConfirmedRequests().add(request);
                }
            } else {
                request.setStatus(ParticipationRequestStatus.REJECTED);
                result.getRejectedRequests().add(request);
            }
        }

        participationRequestRepository.saveAll(requests);
        return result;
    }*/

