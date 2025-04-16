package ru.practicum.comment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentFullDto;
import ru.practicum.comment.dto.CommentModerateDto;
import ru.practicum.comment.enums.CommentState;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/comments/admin") // todo  разделить на user, admin
public class AdminCommentController {
    private final CommentService commentService;

    @PatchMapping("/moderate")
    public List<CommentFullDto> moderateComments(@RequestBody @Valid List<CommentModerateDto> commentModerateDtos) {
        return commentService.moderateComments(commentModerateDtos);
    }

    @GetMapping("/{state}")
    public List<CommentFullDto> getCommentsByState(@PathVariable CommentState state) {
        return commentService.getCommentsByState(state);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByAdmin(@PathVariable Long commentId) {
        commentService.deleteCommentByAdmin(commentId);
    }
}