package ru.practicum.event.dal;

import org.mapstruct.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.BaseUpdateEventRequest;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.UpdateEventAdminRequest;
import ru.practicum.event.model.UpdateEventUserRequest;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EventMapper {
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "requestModeration", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "participantLimit", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    Event eventShortDtoToEvent(EventShortDto eventShortDto);

    @Mapping(target = "views", ignore = true)
    EventShortDto eventToEventShortDto(Event event);

    @Mapping(target = "state", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    Event newEventDtotoEvent(NewEventDto newEventDto);

    EventFullDto eventToEventFullDto(Event event, Long views);
}
