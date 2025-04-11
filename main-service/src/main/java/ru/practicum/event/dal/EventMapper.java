package ru.practicum.event.dal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.category.dal.CategoryMapper;
import ru.practicum.event.EventServiceImpl;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;
import ru.practicum.user.dal.UserMapper;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = {EventRepository.class, EventServiceImpl.class, CategoryMapper.class, UserMapper.class})
public interface EventMapper {

    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category.id", source = "category")
    Event newDtoToEvent(NewEventDto newEventDto);

    EventFullDto toFullDto(Event event, Long views, Long confirmedRequests);

    EventShortDto toShortDto(Event event, Long views, Long confirmedRequests);
}
