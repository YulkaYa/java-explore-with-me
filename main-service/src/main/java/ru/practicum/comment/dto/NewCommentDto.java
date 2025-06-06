package ru.practicum.comment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@Data
@RequiredArgsConstructor
public class NewCommentDto {
    @NotBlank
    @Pattern(regexp = "(\\n*.*\\r*.*)*(.*\\S+.*)*(\\n*.*\\r*.*)*", message = "title не может состоять из пробелов или быть пустым")
    @Size(min = 3, max = 120)
    private String title;

    @NotBlank
    @Pattern(regexp = "(\\n*.*\\r*.*)*(.*\\S+.*)*(\\n*.*\\r*.*)*", message = "text не может состоять из пробелов или быть пустым")
    @Size(min = 20, max = 7000)
    private String text;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @NotNull
    private Long eventId;
}
