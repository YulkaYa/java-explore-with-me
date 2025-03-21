package ru.practicum.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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
import ru.practicum.event.model.BaseUpdateEventRequest;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.UpdateEventAdminRequest;
import ru.practicum.event.model.UpdateEventUserRequest;
import ru.practicum.location.model.Location;
import ru.practicum.user.dal.UserRepository;
import ru.practicum.user.model.User;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper mapper;
    private final StatsClient statsClient;

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

        event = updateFieldsOfEvent(updateEventRequest, event);
        return getEventWithViews(eventRepository.save(event));
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
        }
    }

    @Override
    public List<EventShortDto> getEvents(List users, List states, List categories, String rangeStart, String rangeEnd, int from, int size) {
        return List.of();
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

/*  todo  @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = getEventIfExistOrThrow(null, eventId);

        if (event.getState() != EventState.PENDING && event.getState() != EventState.CANCELED) {
            throw new ConditionsNotMetException("Only pending or canceled events can be updated");
        }
        validateEventDate(updateEventAdminRequest.getEventDate(), 1);

        event = mapper.updateFromDto(updateEventUserRequest, event);

        return getEventWithViews(eventRepository.save(event));
    }*/

}