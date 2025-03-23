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
import ru.practicum.user.dal.UserRepository;
import ru.practicum.user.model.User;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EventServiceImpl implements EventService {
    private EventRepository eventRepository;
    private UserRepository userRepository;
    private CategoryRepository categoryRepository;
    private EventMapper mapper;
    private StatsClient statsClient;

    // Получение событий, добавленных текущим пользователем
    @Override
    public List<EventShortDto> getEventsByUser(Long userId,
                                               int from,
                                               int size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        return eventRepository.findByInitiatorId(userId, page)
                .map(mapper::eventToEventShortDto)
                .getContent();
       // return eventRepository.findByInitiatorId(userId); todo
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

        return mapper.eventToEventFullDto(event, 0L);
    }

    // Получение события по ID и пользователю
    @Override
    public EventFullDto getEventByIdAndUser(Long userId, Long eventId) {
        Event event = getEventIfExistOrThrow(userId, eventId);
        return getEventWithViews(event);
    }

    @Override
    public <T extends BaseUpdateEventRequest> EventFullDto updateEvent(Long userId, Long eventId, T updateEventRequest, long durationHours) {
        Event event = getEventIfExistOrThrow(userId, eventId);

        if (event.getState() != EventState.PENDING && event.getState() != EventState.CANCELED) {
            throw new ConditionsNotMetException("Only pending or canceled events can be updated");
        }
        validateEventDate(updateEventRequest.getEventDate(), durationHours);

        event  = updateFieldsOfEvent(updateEventRequest, event);
        return getEventWithViews(eventRepository.save(event));
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

    private EventFullDto getEventWithViews(Event event) {
        Long eventId = event.getId();
        List<ViewStatsDto> viewStatsDtos = statsClient.getStats(event.getCreatedOn(), LocalDateTime.now(), List.of("/events/" + eventId), false);
        return mapper.eventToEventFullDto(event, viewStatsDtos.getFirst().getHits());
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
        return eventRepository.findEventsByFilters(users, states, categories, rangeStart, rangeEnd, page)
                .map(this::getEventWithViews)
                .getContent();
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
        PageRequest page;
        if ("EVENT_DATE".equals(sort.toUpperCase())) {
            page = PageRequest.of(from / size, size, Sort.by("eventDate"));
        } else if ("VIEWS".equals(sort.toUpperCase())) {
            page = PageRequest.of(from / size, size, Sort.by("views").descending());
        } else {
            page = PageRequest.of(from / size, size);
        }

        return eventRepository.findPublishedEvents(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, page)
                .map(mapper::eventToEventShortDto)
                .getContent();
    }
}