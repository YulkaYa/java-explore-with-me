package ru.practicum.participation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.ConditionsNotMetException;
import ru.practicum.common.NotFoundException;
import ru.practicum.event.EventState;
import ru.practicum.event.dal.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.participation.dal.ParticipationRequestMapper;
import ru.practicum.participation.dal.ParticipationRequestRepository;
import ru.practicum.participation.dto.EventRequestStatusUpdateRequest;
import ru.practicum.participation.dto.EventRequestStatusUpdateResult;
import ru.practicum.participation.dto.ParticipationRequestDto;
import ru.practicum.participation.model.ParticipationRequest;
import ru.practicum.user.dal.UserRepository;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ParticipationRequestServiceImpl implements ParticipationRequestService {

    private final ParticipationRequestRepository participationRequestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final ParticipationRequestMapper mapper;

    // Создание заявки на участие
    @Transactional
    @Override
    public ParticipationRequestDto addParticipationRequest(Long userId, Long eventId) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        // Проверка, что событие опубликовано
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConditionsNotMetException("Event is not published");
        }

        // Проверка, что пользователь не является инициатором события
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConditionsNotMetException("Initiator cannot request participation in their own event");
        }

        // Проверка, что заявка уже существует
        if (participationRequestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
            throw new ConditionsNotMetException("Participation request already exists");
        }

        Integer participantLimit = event.getParticipantLimit();
       long countOfConfirmedRequests =  participationRequestRepository.countByEventIdAndStatus(eventId, ParticipationRequestStatus.CONFIRMED);
       boolean requiredModeration = event.getRequestModeration();
        ParticipationRequest request = new ParticipationRequest();
        request.setCreated(LocalDateTime.now());
        request.setEvent(event);
        request.setRequester(requester);

        // Проверка лимита участников
        if (participantLimit > 0 && participantLimit <= countOfConfirmedRequests) {
            throw new ConditionsNotMetException("Participant limit reached");
        } else if (participantLimit == 0) {
            request.setStatus(ParticipationRequestStatus.CONFIRMED);
        } else {
            if (requiredModeration) {
                request.setStatus(ParticipationRequestStatus.PENDING);
            } else {
                request.setStatus(ParticipationRequestStatus.CONFIRMED);
            }
        }

        return mapper.participationRequestToParticipationRequestDto(participationRequestRepository.save(request));
    }

    // Отмена заявки на участие
    @Transactional
    @Override
    public ParticipationRequestDto cancelParticipationRequest(Long userId, Long requestId) {
        ParticipationRequest request = participationRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found"));

        if (!request.getRequester().getId().equals(userId)) {
            throw new ConditionsNotMetException("User cannot cancel another user's request");
        }

        request.setStatus(ParticipationRequestStatus.CANCELED);
        return mapper.participationRequestToParticipationRequestDto(participationRequestRepository.save(request));
    }

    // Получение заявок текущего пользователя на участие
    @Override
    public List<ParticipationRequestDto> getParticipationsByRequesterId(Long userId) {
       List<ParticipationRequest> list = participationRequestRepository.findByRequesterId(userId);
        return mapper.toListParticipationRequestDto(list);
    }

    // Получение заявок на участие в событии текущего пользователя
    @Override
    public List<ParticipationRequestDto> getEventParticipants(Long userId, Long eventId) {
        eventRepository.findByIdAndInitiatorId(eventId,userId).orElseThrow(() -> new NotFoundException("No such event with such initiatorId"));
        List<ParticipationRequest> participationRequests = (participationRequestRepository.findByEventId(eventId));
        return mapper.toListParticipationRequestDto(participationRequests);
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequest) {
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        ParticipationRequestStatus updateStatus = updateRequest.getStatus();
        ParticipationRequestStatus confirmed = ParticipationRequestStatus.CONFIRMED;
        ParticipationRequestStatus rejected = ParticipationRequestStatus.REJECTED;

        if (!updateStatus.equals(confirmed) && !updateStatus.equals(rejected)) {
            throw new ConditionsNotMetException("Incorrect update status in update request");
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new RuntimeException("Only the event initiator can update request status");
        }

        List<ParticipationRequest> requests = participationRequestRepository.findAllByIdIn(updateRequest.getRequestIds());

        long limit = event.getParticipantLimit();
        long countOfConfirmedRequests = participationRequestRepository.countByEventIdAndStatus(eventId, confirmed);

        if (limit <= countOfConfirmedRequests) {
            throw new ConditionsNotMetException("The participant limit has been reached");
        }

            for (ParticipationRequest request : requests) {
                if ((limit != 0) && (event.getRequestModeration())) {

                if (!request.getStatus().equals(ParticipationRequestStatus.PENDING)) {
                    throw new ConditionsNotMetException("Request must be in PENDING state");
                }

                    if (limit > countOfConfirmedRequests && updateStatus.equals(confirmed)) {
                        request.setStatus(confirmed);
                        result.getConfirmedRequests().add(mapper.participationRequestToParticipationRequestDto(request));
                        countOfConfirmedRequests++;
                    } else {
                        request.setStatus(rejected);
                        result.getRejectedRequests().add(mapper.participationRequestToParticipationRequestDto(request));
                    }

            } else {
                    request.setStatus(confirmed);
                    result.getConfirmedRequests().add(mapper.participationRequestToParticipationRequestDto(request));
                }
            }
            participationRequestRepository.saveAll(requests);
        return result;
    }
}