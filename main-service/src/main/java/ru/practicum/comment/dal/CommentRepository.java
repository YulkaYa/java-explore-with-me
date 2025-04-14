package ru.practicum.comment.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.comment.enums.CommentState;
import ru.practicum.comment.model.Comment;
import ru.practicum.participation.ParticipationRequestStatus;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {


    List<Comment> findAllByCreatorId(Long userId);

    List<Comment> findAllByState(CommentState state);

    List<Comment> findAllByEventIdAndState(Long eventId, CommentState state);

    boolean existsByIdAndCreatorId(Long commentId, Long userId);

}