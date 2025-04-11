package ru.practicum.event;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.EndpointHitDto;
import ru.practicum.StatsClient;
import ru.practicum.ViewStatsDto;
import ru.practicum.category.dal.CategoryRepository;
import ru.practicum.category.model.Category;
import ru.practicum.common.ConditionsNotMetException;
import ru.practicum.common.NotFoundException;
import ru.practicum.event.dal.EventMapper;
import ru.practicum.event.dal.EventRepository;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.Event;
import ru.practicum.participation.ParticipationRequestStatus;
import ru.practicum.participation.dal.ParticipationRequestRepository;
import ru.practicum.user.dal.UserRepository;
import ru.practicum.user.model.User;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper mapper;
    private final StatsClient statsClient;
    private final ParticipationRequestRepository participationRequestRepository;
    @Value("${app-name}")
    private String appName;

    // Получение событий, добавленных текущим пользователем
    @Override
    public List<EventShortDto> getEventsByUser(Long userId,
                                               int from,
                                               int size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        List<Event> events = eventRepository.findByInitiatorId(userId, page).getContent();
        return getShortDtosFromEvents(events);
    }

    public List<EventShortDto> getShortDtosFromEvents(List<Event> events) {
        if (events.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> eventIds = events.stream().map(Event::getId).toList();
        Map<Long, Long> eventIdsAndConfirmedRequests = getConfirmedRequestsByEventIds(eventIds);
        Map<Long, Long> eventIdsAndViews = getViewsByEventIds(events);

        return events.stream()
                .map(event -> {
                    Long eventId = event.getId();

                    Long views = eventIdsAndViews.get(eventId);
                    views = views == null ? 0 : views;
                    Long confirmedRequests = eventIdsAndConfirmedRequests.get(eventId);

                    confirmedRequests = confirmedRequests == null ? 0 : confirmedRequests;
                    // формируем нужную информацию
                    return mapper.toShortDto(event, views, confirmedRequests);
                })
                .collect(Collectors.toList());
    }

    public List<EventFullDto> getFullDtosFromEvents(List<Event> events) {
        if (events.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> eventIds = events.stream().map(Event::getId).toList();
        Map<Long, Long> eventIdsAndConfirmedRequests = getConfirmedRequestsByEventIds(eventIds);
        Map<Long, Long> eventIdsAndViews = getViewsByEventIds(events);

        return events.stream()
                .map(event -> {
                    Long eventId = event.getId();

                    Long views = eventIdsAndViews.get(eventId);
                    views = views == null ? 0 : views;

                    Long confirmedRequests = eventIdsAndConfirmedRequests.get(eventId);
                    confirmedRequests = confirmedRequests == null ? 0 : confirmedRequests;

                    return mapper.toFullDto(event, views, confirmedRequests);
                })
                .collect(Collectors.toList());
    }

    // Добавление нового события
    @Transactional
    @Override
    public EventFullDto add(Long userId, NewEventDto newEventDto) {

        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Category not found"));
        validateEventDate(newEventDto.getEventDate(), 2);

        Event event = mapper.newDtoToEvent(newEventDto);
        event.setInitiator(initiator);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);
        event = eventRepository.save(event);

        return mapper.toFullDto(event, 0L, 0L);
    }

    // Получение события по ID и пользователю
    @Override
    public EventFullDto getByIdAndUser(Long userId, Long eventId) {
        Event event = getIfExistOrThrow(userId, eventId);
        return getFullDtosFromEvents(List.of(event)).getFirst();
    }

    @Transactional
    @Override
    public <T extends BaseUpdateEventRequest> EventFullDto update(Long userId, Long eventId, T updateEventRequest, long durationHours) {
        Event event = getIfExistOrThrow(userId, eventId);

        if (event.getState() != EventState.PENDING && event.getState() != EventState.CANCELED) {
            throw new ConditionsNotMetException("Only pending or canceled events can be updated");
        }

        if (updateEventRequest.getEventDate() != null) {
            validateEventDate(updateEventRequest.getEventDate(), durationHours);
        }

        event = updateFields(updateEventRequest, event);
        return getFullDtosFromEvents(List.of(eventRepository.save(event))).getFirst();
    }

    private static void validateEventDate(LocalDateTime eventDate, long hours) {
        LocalDateTime now = LocalDateTime.now();
        if (Duration.between(now, eventDate).toHours() < hours) {
            throw new ConditionsNotMetException("Event date should be more than two hours after now");
        }
    }

    private Event getIfExistOrThrow(Long userId, Long eventId) {
        Event event;
        if (userId == null) {
            event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new NotFoundException("Event not found"));
        } else {
            event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                    .orElseThrow(() -> new NotFoundException("Event not found"));
        }
        return event;
    }

    private <T extends BaseUpdateEventRequest> Event updateFields(T updateEventRequest, Event event) {

        if (updateEventRequest.getCategory() != null) {
            event.setCategory(categoryRepository.findById(updateEventRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException("Category not found")));
        }

        if (updateEventRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventRequest.getAnnotation());
        }

        if (updateEventRequest.getDescription() != null) {
            event.setDescription(updateEventRequest.getDescription());
        }

        if (updateEventRequest.getEventDate() != null) {
            event.setEventDate(updateEventRequest.getEventDate());
        }

        if (updateEventRequest.getLocation() != null) {
            event.setLocation(updateEventRequest.getLocation());
        }

        if (updateEventRequest.getPaid() != null) {
            event.setPaid(updateEventRequest.getPaid());
        }

        if (updateEventRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventRequest.getParticipantLimit());
        }

        if (updateEventRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventRequest.getRequestModeration());
        }

        if (updateEventRequest.getTitle() != null) {
            event.setTitle(updateEventRequest.getTitle());
        }

        if (updateEventRequest.getClass().getSimpleName().equals("UpdateEventAdminRequest")) {
            AdminStateAction adminStateAction = ((UpdateEventAdminRequest) updateEventRequest).getStateAction();
            if (adminStateAction != null) {
                if (event.getState().equals(EventState.PENDING)) {
                    if (adminStateAction.equals(AdminStateAction.PUBLISH_EVENT)) {
                        event.setState(EventState.PUBLISHED);
                        event.setPublishedOn(LocalDateTime.now());
                    } else if (adminStateAction.equals(AdminStateAction.REJECT_EVENT)) {
                        event.setState(EventState.CANCELED);
                    }
                } else {
                    throw new ConditionsNotMetException("Only pending events can be updated");
                }
            }
        }

        if (updateEventRequest.getClass().getSimpleName().equals("UpdateEventUserRequest")) {
            UserStateAction userStateAction = ((UpdateEventUserRequest) updateEventRequest).getStateAction();
            if (userStateAction != null) {
                if (userStateAction.equals(UserStateAction.SEND_TO_REVIEW)) {
                    event.setState(EventState.PENDING);
                    event.setPublishedOn(null);
                } else if (userStateAction.equals(UserStateAction.CANCEL_REVIEW)) {
                    event.setState(EventState.CANCELED);
                    event.setPublishedOn(null);
                }
            }
        }
        return event;
    }

    // Получение событий с фильтрами
    public List<EventFullDto> getEvents(
            List<Long> users,
            List<EventState> states,
            List<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            int from,
            int size) {

        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        return getFullDtosFromEvents(eventRepository.findEventsByFilters(users, states, categories, rangeStart,
                rangeEnd, page).getContent());
    }

    // Получение опубликованных событий с фильтрами
    public List<EventShortDto> getPublishedEvents(
            String text,
            List<Long> categories,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Boolean onlyAvailable,
            String sort,
            int from,
            int size,
            HttpServletRequest httpServletRequest) {

        // Если диапазон дат не указан, выбираем события, которые произойдут позже текущего времени
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException("Start date should be before end date");
        }

        // Создание объекта Pageable для пагинации и сортировки
        PageRequest page = PageRequest.of(from / size, size);

        Map<Long, Event> mapEvents = eventRepository.findPublishedEvents(
                text,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                page).getContent().stream().collect(Collectors.toMap(Event::getId, event -> event));

        sendStats(httpServletRequest);
        List<EventShortDto> eventsShortDtos = getShortDtosFromEvents(mapEvents.values().stream().toList());

        if (sort != null) {
            if ("EVENT_DATE".equalsIgnoreCase(sort)) {
                eventsShortDtos.sort(Comparator.comparing(EventShortDto::getEventDate));
            } else if ("VIEWS".equalsIgnoreCase(sort)) {
                eventsShortDtos.sort(Comparator.comparing(EventShortDto::getViews));
            }
        }

        if (onlyAvailable != null && onlyAvailable) {
            for (EventShortDto eventShortDto : eventsShortDtos) {
                Integer limit = mapEvents.get(eventShortDto.getId()).getParticipantLimit();
                if (!(eventShortDto.getConfirmedRequests() < limit || limit == 0)) {
                    eventsShortDtos.remove(eventShortDto);
                }
            }
        }
        return eventsShortDtos;
    }

    private void sendStats(HttpServletRequest httpServletRequest) {
        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app(appName)
                .uri(httpServletRequest.getRequestURI())
                .ip(httpServletRequest.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();
        statsClient.saveHit(endpointHitDto);
    }

    @Override
    public EventFullDto getPublishedEventById(Long id, HttpServletRequest httpServletRequest) {
        sendStats(httpServletRequest);

        Event event = eventRepository.findById(id).orElseThrow(() -> new NotFoundException("Event with id not found"));
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("EventStatus is not Published");
        }
        return getFullDtosFromEvents(List.of(event)).getFirst();
    }

    private Map<Long, Long> getViewsByEventIds(List<Event> events) {
        List<String> uris = new ArrayList<>();
        LocalDateTime startDate = LocalDateTime.of(2000, 1, 1, 0, 0, 0, 0);
        if (events.size() > 1) {
            startDate = events.stream()
                    .map(Event::getCreatedOn)
                    .min(LocalDateTime::compareTo)
                    .orElse(LocalDateTime.of(2000, 1, 1, 0, 0, 0, 0));
        }

        events.forEach(event -> uris.add("/events/" + event.getId()));
        List<ViewStatsDto> viewStatsDtos = statsClient.getStats(startDate.minusHours(1), LocalDateTime.now().plusHours(1), uris, true);
        Map<Long, Long> result = new HashMap<>();
        viewStatsDtos
                .forEach(viewStatsDto ->
                        result.put((Long.valueOf(viewStatsDto.getUri().replace("/events/", ""))),
                                viewStatsDto.getHits()));
        return result;
    }

    private Map<Long, Long> getConfirmedRequestsByEventIds(List<Long> eventIds) {

        Map<Long, Long> participationCounts = participationRequestRepository.countGroupedByEventIdAndStatusToMap(eventIds,
                ParticipationRequestStatus.CONFIRMED);

        return participationCounts;
    }
}