package ru.practicum.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.service.CommentService;
import ru.practicum.comment.dto.CommentEditDto;
import ru.practicum.comment.dto.CommentFullDto;
import ru.practicum.comment.dto.NewCommentDto;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/comments/user")
public class UserCommentController {
    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentFullDto createComment(@RequestBody @Valid NewCommentDto newCommentDto,
                                        @RequestParam Long userId) {
        return commentService.createComment(newCommentDto, userId);
    }

    @GetMapping("/{commentId}")
    public CommentFullDto getComment(@PathVariable Long commentId) {
        return commentService.getCommentById(commentId);
    }

    @GetMapping()
    public List<CommentFullDto> getUserComments(@RequestParam Long userId) {
        return commentService.getUserComments(userId);
    }

    @PatchMapping()
    public CommentFullDto updateCommentByUser(@RequestBody @Valid CommentEditDto commentEditDto,
                                              @RequestParam Long userId) {
        return commentService.updateCommentByUser(commentEditDto, userId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByUser(@PathVariable Long commentId,
                                    @RequestParam Long userId) {
        commentService.deleteCommentByUser(commentId, userId);
    }
}