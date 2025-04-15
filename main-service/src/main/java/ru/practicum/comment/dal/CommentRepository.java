package ru.practicum.comment.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.comment.enums.CommentState;
import ru.practicum.comment.model.Comment;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByCreatorId(Long userId);

    List<Comment> findAllByState(CommentState state);

    List<Comment> findAllByEventIdAndState(Long eventId, CommentState state);

    boolean existsByIdAndCreatorId(Long commentId, Long userId);

}