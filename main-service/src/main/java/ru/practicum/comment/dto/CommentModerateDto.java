package ru.practicum.comment.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.practicum.comment.enums.CommentAdminAction;

@SuperBuilder(toBuilder = true)
@Data
@RequiredArgsConstructor
public class CommentModerateDto {
    @NotNull
    private Long id;

    @NotBlank
    @Pattern(regexp = "(\\n*.*\\r*.*)*(.*\\S+.*)*(\\n*.*\\r*.*)*", message = "adminComment не может состоять из пробелов или быть пустым")
    @Size(min = 20, max = 7000)
    private String adminComment;

    @NotNull
    private CommentAdminAction adminAction;
}
