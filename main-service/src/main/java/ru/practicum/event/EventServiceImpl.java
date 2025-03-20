package ru.practicum.event;

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
import ru.practicum.event.model.Event;
import ru.practicum.event.model.UpdateEventUserRequest;
import ru.practicum.user.dal.UserRepository;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

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
        validateEventDate(newEventDto.getEventDate());

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

    // Изменение события
    @Transactional
    @Override
    public EventFullDto updateEvent(Long userId, Long eventId,
                                    UpdateEventUserRequest updateEventUserRequest) {
        Event event = getEventIfExistOrThrow(userId, eventId);

        if (event.getState() != EventState.PENDING && event.getState() != EventState.CANCELED) {
            throw new ConditionsNotMetException("Only pending or canceled events can be updated");
        }
        validateEventDate(updateEventUserRequest.getEventDate());

        event = mapper.updateFromDto(updateEventUserRequest, event);

        return getEventWithViews(eventRepository.save(event));
    }

    private static void validateEventDate(LocalDateTime eventDate) {
        LocalDateTime now = LocalDateTime.now();
        if(eventDate.compareTo(now) < 2) {
            throw  new ConditionsNotMetException("Event date should be more than two hours after now");
        }
    }

    private Event getEventIfExistOrThrow(Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event not found"));
        return event;
    }

    private EventFullDto getEventWithViews(Event event) {
        Long eventId = event.getId();
        List<ViewStatsDto> viewStatsDtos = statsClient.getStats(event.getCreatedOn(), LocalDateTime.now(), List.of("/events/" + eventId), false);
        return mapper.eventToEventFullDto(event, viewStatsDtos.getFirst().getHits());
    }
}