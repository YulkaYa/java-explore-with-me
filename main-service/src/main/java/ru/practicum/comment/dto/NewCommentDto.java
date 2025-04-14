package ru.practicum.comment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@Data
@RequiredArgsConstructor
public class NewCommentDto {
    @NotBlank
    @Pattern(regexp = ".*\\S+.*", message = "Title не может состоять из пробелов или быть пустым")
    @Size(min = 20, max = 2000)
    private String title;

    @NotBlank
    @Pattern(regexp = ".*\\S+.*", message = "Text не может состоять из пробелов или быть пустым")
    @Size(min = 20, max = 7000)
    private String text;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long creatorId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long eventId;
}
