package ru.practicum.comment.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.comment.enums.CommentState;
import ru.practicum.comment.model.Comment;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c " +
            "LEFT JOIN FETCH c.event AND LEFT JOIN FETCH c.creator " +
            "LEFT JOIN FETCH c.event.category " +
            "WHERE (c.creator.id = :userId ) ORDER BY c.id ")
            List<Comment> findAllByCreatorId(Long userId);

    @Query("SELECT c FROM Comment c " +
            "LEFT JOIN FETCH c.event AND LEFT JOIN FETCH c.creator " +
            "LEFT JOIN FETCH c.event.category " +
            "WHERE (c.state = :state ) ORDER BY c.id ")
    List<Comment> findAllByState(CommentState state);

    @Query("SELECT c FROM Comment c " +
            "LEFT JOIN FETCH c.event AND LEFT JOIN FETCH c.creator " +
            "LEFT JOIN FETCH c.event.category " +
            "WHERE (c.event.id = :eventId AND c.state = :state ) ORDER BY c.id ")
    List<Comment> findAllByEventIdAndState(Long eventId, CommentState state);

    boolean existsByIdAndCreatorId(Long commentId, Long userId);

}