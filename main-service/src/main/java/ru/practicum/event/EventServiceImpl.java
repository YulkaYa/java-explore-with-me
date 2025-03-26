package ru.practicum.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsClient;
import ru.practicum.ViewStatsDto;
import ru.practicum.category.dal.CategoryRepository;
import ru.practicum.category.model.Category;
import ru.practicum.common.ConditionsNotMetException;
import ru.practicum.common.NotFoundException;
import ru.practicum.event.dal.EventMapper;
import ru.practicum.event.dal.EventRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.BaseUpdateEventRequest;
import ru.practicum.event.model.Event;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.participation.ParticipationRequestStatus;
import ru.practicum.participation.dal.ParticipationRequestRepository;
import ru.practicum.participation.dto.RequestsCount;
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

    // Получение событий, добавленных текущим пользователем
    @Override
    public List<EventShortDto> getEventsByUser(Long userId,
                                               int from,
                                               int size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        List<Event> events = eventRepository.findByInitiatorId(userId, page).getContent();

        return getEventShortDtosFromEvents(events);

        // return eventRepository.findByInitiatorId(userId); todo
    }

    private List<EventShortDto> getEventShortDtosFromEvents(List<Event> events) {
        List<Long> eventIds = events.stream().map(Event::getId).toList();
        Map<Long,Integer> eventIdsAndConfirmedRequests = getConfirmedRequestsByEventIds(eventIds);
        Map<Long, Long> eventIdsAndViews = getViewsByEventIds(events);

        Map<Long, Long> eventsIdsAndCategoriesIds = eventRepository.findCategoryByEventIdIn(eventIds);
        Map<Long, Category> mapOfCategories= categoryRepository.findAllById(eventsIdsAndCategoriesIds.values())
                .stream()
                .collect(Collectors.toMap(Category::getId, category -> category));

        return events.stream()
                .map(event -> {
                    //todo setInitiator?
                    Long eventId = event.getId();
                    Category category = mapOfCategories.get(eventsIdsAndCategoriesIds.get(eventId));
                    Long views = eventIdsAndViews.get(eventId);
                    Integer confirmedRequests = eventIdsAndConfirmedRequests.get(eventId);
                    event.setCategory(category);
                    // формируем нужную информацию
                    return mapper.eventToEventShortDto(event, views, confirmedRequests);
                })
                .collect(Collectors.toList());
    }

    private List<EventFullDto> getEventFullDtosFromEvents(List<Event> events) {
        List<Long> eventIds = events.stream().map(Event::getId).toList();
        Map<Long,Integer> eventIdsAndConfirmedRequests = getConfirmedRequestsByEventIds(eventIds);
        Map<Long, Long> eventIdsAndViews = getViewsByEventIds(events);

        Map<Long, Long> eventsIdsAndCategoriesIds = eventRepository.findCategoryByEventIdIn(eventIds);
        Map<Long, Category> mapOfCategories= categoryRepository.findAllById(eventsIdsAndCategoriesIds.values())
                .stream()
                .collect(Collectors.toMap(Category::getId, category -> category));

        return events.stream()
                .map(event -> {
                    //todo setInitiator?
                    Long eventId = event.getId();
                    Category category = mapOfCategories.get(eventsIdsAndCategoriesIds.get(eventId));
                    Long views = eventIdsAndViews.get(eventId);
                    Integer confirmedRequests = eventIdsAndConfirmedRequests.get(eventId);
                    event.setCategory(category);
                    // формируем нужную информацию
                    return mapper.eventToEventFullDto(event, views, confirmedRequests);
                })
                .collect(Collectors.toList());
    }

    // Добавление нового события
    @Transactional
    @Override
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {

        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Category not found"));
        validateEventDate(newEventDto.getEventDate(), 2);

        Event event = mapper.newEventDtotoEvent(newEventDto);
        event.setInitiator(initiator);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);
        event  = eventRepository.save(event);

        return mapper.eventToEventFullDto(event, 0L, 0);
    }

    // Получение события по ID и пользователю
    @Override
    public EventFullDto getEventByIdAndUser(Long userId, Long eventId) {
        Event event = getEventIfExistOrThrow(userId, eventId);
        return getEventFullDtosFromEvents(List.of(event)).getFirst();
    }

    @Override
    public <T extends BaseUpdateEventRequest> EventFullDto updateEvent(Long userId, Long eventId, T updateEventRequest, long durationHours) {
        Event event = getEventIfExistOrThrow(userId, eventId);

        if (event.getState() != EventState.PENDING && event.getState() != EventState.CANCELED) {
            throw new ConditionsNotMetException("Only pending or canceled events can be updated");
        }
        validateEventDate(updateEventRequest.getEventDate(), durationHours);

        event  = updateFieldsOfEvent(updateEventRequest, event);

        return getEventFullDtosFromEvents(List.of(eventRepository.save(event))).getFirst();
    }

    private static void validateEventDate(LocalDateTime eventDate, long hours) {
        LocalDateTime now = LocalDateTime.now();
        if(Duration.between(now, eventDate).toHours() < hours ) {
            throw  new ConditionsNotMetException("Event date should be more than two hours after now");
        }
    }

    private Event getEventIfExistOrThrow(Long userId, Long eventId) {
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

    private <T extends BaseUpdateEventRequest> Event updateFieldsOfEvent(T updateEventRequest, Event event) {

        if (updateEventRequest.getCategory() != null) {
            event.setCategory(categoryRepository.findById(updateEventRequest.getCategory())
                    .orElseThrow(()-> new NotFoundException("Category not found")));
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
            if (adminStateAction.equals(AdminStateAction.PUBLISH_EVENT)) {
                event.setState(EventState.PUBLISHED);
            } else if (adminStateAction.equals(AdminStateAction.REJECT_EVENT)) {
                event.setState(EventState.CANCELED);
            }
        }

        if (updateEventRequest.getClass().getSimpleName().equals("UpdateEventUserRequest")) {
            UserStateAction userStateAction = ((UpdateEventUserRequest) updateEventRequest).getStateAction();
            if (userStateAction.equals(UserStateAction.SEND_TO_REVIEW)) {
                event.setState(EventState.PENDING);
            } else if (userStateAction.equals(UserStateAction.CANCEL_REVIEW)) {
                event.setState(EventState.CANCELED);
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

/*        // Преобразование списка состояний (states) в список EventState
        List<EventState> eventStates = null;
        if (states != null) {
            eventStates = states.stream()
                    .map(EventState::valueOf)
                    .toList();
        } todo */

        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        return getEventFullDtosFromEvents(eventRepository.findEventsByFilters(users, states, categories, rangeStart,
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
            int size) {

        // Если диапазон дат не указан, выбираем события, которые произойдут позже текущего времени
        if (rangeStart == null && rangeEnd == null) {
            rangeStart = LocalDateTime.now();
        }

        // Создание объекта Pageable для пагинации и сортировки
        PageRequest page = PageRequest.of(from / size, size);

        Map<Long, Event> mapEvents =
                eventRepository.findPublishedEvents(text, categories, paid, rangeStart, rangeEnd,
                        page).getContent().stream().collect(Collectors.toMap(Event::getId, event -> event));
        List<EventShortDto> eventsShortDtos = getEventShortDtosFromEvents(mapEvents.values().stream().toList());

            if ("EVENT_DATE".equals(sort.toUpperCase())) {
                eventsShortDtos.sort(Comparator.comparing(EventShortDto::getEventDate));
            } else if ("VIEWS".equals(sort.toUpperCase())) {
                eventsShortDtos.sort(Comparator.comparing(EventShortDto::getViews));
        }

            if (onlyAvailable) {
                for (EventShortDto eventShortDto: eventsShortDtos) {
                    Integer limit = mapEvents.get(eventShortDto.getId()).getParticipantLimit();
                    if (!(eventShortDto.getConfirmedRequests() < limit || limit == 0)) {
                        eventsShortDtos.remove(eventShortDto);
                    }
                }
            }
        return eventsShortDtos;
    }

    @Override
    public EventFullDto getPublishedEventById(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new NotFoundException("Event with id not found"));
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("EventStatus is not Published");
        }
        return getEventFullDtosFromEvents(List.of(event)).getFirst();
    }

    private Map<Long, Long> getViewsByEventIds(List<Event> events) {
        List<String> uris = new ArrayList<>();
        LocalDateTime startDate = LocalDateTime.now();
        for (Event event: events) {
            uris.add("/events/" + event.getId());
            if (event.getPublishedOn().isBefore(startDate)) {
                startDate = event.getPublishedOn();
            }
        }
        List<ViewStatsDto> viewStatsDtos = statsClient.getStats(startDate, LocalDateTime.now(), uris, false);
        Map<Long, Long> result = new HashMap<>();
        viewStatsDtos
                .stream()
                .peek(viewStatsDto -> result.put(Long.getLong(viewStatsDto.getUri().replace("/events/", "")), viewStatsDto.getHits()));
        return result;
    }

    private Map<Long, Integer> getConfirmedRequestsByEventIds(List<Long> eventIds) {
        // Получаем подтвержденные заявки по id событий
        return  participationRequestRepository.countGroupedByEventIdAndStatus(eventIds, ParticipationRequestStatus.CONFIRMED);
    }
}