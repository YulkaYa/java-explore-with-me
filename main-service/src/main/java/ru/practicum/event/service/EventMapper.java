package ru.practicum.event.service;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.category.service.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.comment.dto.CommentShortDto;
import ru.practicum.event.dal.EventRepository;
import ru.practicum.event.model.EventState;
import ru.practicum.event.dto.BaseEvent;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;
import ru.practicum.user.service.UserMapper;
import ru.practicum.user.model.User;
import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = {EventRepository.class, EventServiceImpl.class, CategoryMapper.class, UserMapper.class})
public interface EventMapper {

    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "category")
    Event newDtoToEvent(NewEventDto newEventDto, User initiator, EventState state, LocalDateTime createdOn, Category category);

    EventFullDto toFullDto(Event event, Long views, Long confirmedRequests, List<CommentShortDto> comments);

    EventShortDto toShortDto(Event event, Long views, Long confirmedRequests, List<CommentShortDto> comments);

    BaseEvent toBaseEvent(Event event);
}
