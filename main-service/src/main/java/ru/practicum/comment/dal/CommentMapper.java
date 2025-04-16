package ru.practicum.comment.dal;

import org.mapstruct.*;
import ru.practicum.category.dal.CategoryMapper;
import ru.practicum.comment.dto.CommentFullDto;
import ru.practicum.comment.dto.CommentShortDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.event.EventServiceImpl;
import ru.practicum.event.dal.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.user.dal.UserMapper;
import ru.practicum.user.model.User;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = {EventMapper.class, EventServiceImpl.class, UserMapper.class, CategoryMapper.class})
public interface CommentMapper {
    CommentFullDto toCommentFullDto(Comment comment);

    CommentShortDto toCommentShortDto(Comment comment);

    List<CommentShortDto> toListCommentShortDto(List<Comment> commentList);

    List<CommentFullDto> toListCommentFullDto(List<Comment> commentList);


    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creator", source = "userId", qualifiedByName = "mapUser")
    @Mapping(target = "event", source = "newCommentDto.eventId", qualifiedByName = "mapEvent")
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "adminComment", ignore = true)
    Comment toComment(NewCommentDto newCommentDto, Long userId);

    @Named("mapUser")
    default User mapUser(Long userId) {
        if (userId == null) {
            return null;
        }
        User user = new User();
        user.setId(userId);
        return user;
    }

    @Named("mapEvent")
    default Event mapEvent(Long eventId) {
        if (eventId == null) {
            return null;
        }
        Event event = new Event();
        event.setId(eventId);
        return event;
    }
}