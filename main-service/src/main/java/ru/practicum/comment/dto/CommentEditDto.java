package ru.practicum.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.practicum.comment.enums.CommentUserAction;

@SuperBuilder(toBuilder = true)
@Data
@RequiredArgsConstructor
public class CommentEditDto {
    @NotNull
    private Long id;

    @NotBlank
    @Pattern(regexp = "(\\n*.*\\r*.*)*(.*\\S+.*)*(\\n*.*\\r*.*)*", message = "title не может состоять из пробелов или быть пустым")
    @Size(min = 3, max = 120)
    private String title;

    @NotBlank
    @Pattern(regexp = "(\\n*.*\\r*.*)*(.*\\S+.*)*(\\n*.*\\r*.*)*", message = "text не может состоять из пробелов или быть пустым")
    @Size(min = 20, max = 7000)
    private String text;

    @NotNull
    private CommentUserAction userAction = CommentUserAction.SEND_TO_REVIEW;
}
