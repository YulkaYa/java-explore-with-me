package ru.practicum.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dal.CommentMapper;
import ru.practicum.comment.dal.CommentRepository;
import ru.practicum.comment.dto.*;
import ru.practicum.comment.enums.CommentAdminAction;
import ru.practicum.comment.enums.CommentState;
import ru.practicum.comment.enums.CommentUserAction;
import ru.practicum.comment.model.Comment;
import ru.practicum.common.ConditionsNotMetException;
import ru.practicum.common.NotFoundException;
import ru.practicum.event.EventState;
import ru.practicum.event.dal.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.user.dal.UserRepository;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public CommentFullDto createComment(NewCommentDto newCommentDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));
        Event event = eventRepository.findById(newCommentDto.getEventId())
                .orElseThrow(() -> new NotFoundException("Event with id=" + newCommentDto.getEventId() + " not found"));
        if (event.getState() != EventState.PUBLISHED) {
            throw new ConditionsNotMetException("Event should be in Published status");
        }
        Comment comment = commentMapper.toComment(newCommentDto, userId);
        comment.setCreator(user);
        comment.setEvent(event);
        comment.setState(CommentState.PENDING);

        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toCommentFullDto(savedComment);
    }

    @Override
    public CommentFullDto getCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + " not found"));
        return commentMapper.toCommentFullDto(comment);
    }

    @Override
    public List<CommentShortDto> getPublishedCommentsByEventId(Long eventId) {
        List<Comment> comments = commentRepository.findAllByEventIdAndState(eventId, CommentState.PUBLISHED);
        return comments.stream()
                .map(commentMapper::toCommentShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentFullDto> getUserComments(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));
        List<Comment> comments = commentRepository.findAllByCreatorId(userId);
        return comments.stream()
                .map(commentMapper::toCommentFullDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentFullDto updateCommentByUser(CommentEditDto commentEditDto, Long userId) {
        Comment comment = commentRepository.findById(commentEditDto.getId())
                .orElseThrow(() -> new NotFoundException("Comment with id=" + commentEditDto.getId() + " not found"));

        if (!comment.getCreator().getId().equals(userId)) {
            throw new ConditionsNotMetException("User is not the creator of the comment");
        }

        if (comment.getState() == CommentState.PUBLISHED) {
            throw new ConditionsNotMetException("Published comment cannot be edited");
        } // todo может убрать

        comment.setTitle(commentEditDto.getTitle());
        comment.setText(commentEditDto.getText());

        if (commentEditDto.getUserAction() == CommentUserAction.CANCEL_REVIEW) {
            comment.setState(CommentState.CANCELED);
        } else {
            comment.setState(CommentState.PENDING);
        }

        Comment updatedComment = commentRepository.save(comment);
        return commentMapper.toCommentFullDto(updatedComment);
    }

    @Override
    @Transactional
    public List<CommentFullDto> moderateComments(List<CommentModerateDto> commentModerateDtos) {
        if (commentModerateDtos == null) {
            return new ArrayList<>();
        } else {
            List<CommentFullDto> commentFullDtos = new ArrayList<>();
            for (CommentModerateDto commentModerateDto: commentModerateDtos) {
                Comment comment = commentRepository.findById(commentModerateDto.getId())
                        .orElseThrow(() -> new NotFoundException("Comment with id=" + commentModerateDto.getId() + " not found"));

                if (comment.getState() != CommentState.PENDING) {
                    throw new ConditionsNotMetException("Only Pending comment can be edited");
                }

                if (commentModerateDto.getAdminAction() == CommentAdminAction.PUBLISH_COMMENT) {
                    comment.setState(CommentState.PUBLISHED);
                    comment.setPublishedOn(LocalDateTime.now());
                } else {
                    comment.setState(CommentState.CANCELED);
                    comment.setPublishedOn(null);
                }
                comment.setAdminComment(commentModerateDto.getAdminComment());

                Comment updatedComment = commentRepository.save(comment);
                commentFullDtos.add(commentMapper.toCommentFullDto(updatedComment));
            }
         return commentFullDtos;
        }
    }

    @Override
    @Transactional
    public void deleteCommentByUser(Long commentId, Long userId) {
        if (!commentRepository.existsByIdAndCreatorId(commentId, userId)) {
            throw new NotFoundException("Comment with id=" + commentId + " not found for user with id=" + userId);
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional
    public void deleteCommentByAdmin(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new NotFoundException("Comment with id=" + commentId + " not found");
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    public List<CommentFullDto> getCommentsByState(CommentState state) {
        return commentMapper.toListCommentFullDto(commentRepository.findAllByState(state));
    }
}