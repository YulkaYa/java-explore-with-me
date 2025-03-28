package ru.practicum.event.dal;

import org.mapstruct.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;

import java.time.LocalDateTime;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = { EventRepository.class })
public interface EventMapper {

    EventShortDto eventToEventShortDto(Event event, Long views, Integer confirmedRequests);

    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category.id", source = "category")
    Event newEventDtotoEvent(NewEventDto newEventDto);

    EventFullDto eventToEventFullDto(Event event, Long views, Integer confirmedRequests);

}
