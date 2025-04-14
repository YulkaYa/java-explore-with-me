package ru.practicum.comment;

import ru.practicum.comment.dto.*;
import ru.practicum.comment.enums.CommentState;

import java.util.List;

public interface CommentService {
    CommentFullDto createComment(NewCommentDto newCommentDto, Long userId);

    CommentFullDto getCommentById(Long commentId);

    List<CommentShortDto> getCommentsByEventId(Long eventId);

    List<CommentFullDto> getUserComments(Long userId);

    CommentFullDto updateCommentByUser(CommentEditDto commentEditDto, Long userId);

    CommentFullDto moderateComment(CommentModerateDto commentModerateDto);

    void deleteCommentByUser(Long commentId, Long userId);

    void deleteCommentByAdmin(Long commentId);

    List<CommentFullDto> getCommentsByState(CommentState state);
}