package ru.practicum.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.practicum.comment.enums.CommentState;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;

@SuperBuilder(toBuilder = true)
@Data
@RequiredArgsConstructor
public class CommentFullDto {
    @NotNull
    private Long id;

    @NotBlank
    @Pattern(regexp = ".*\\S+.*", message = "Title не может состоять из пробелов или быть пустым")
    @Size(min = 20, max = 2000)
    private String title;

    @NotBlank
    @Pattern(regexp = ".*\\S+.*", message = "Text не может состоять из пробелов или быть пустым")
    @Size(min = 20, max = 7000)
    private String text;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserShortDto creator;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private EventShortDto event;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;

    private CommentState state;

    @Pattern(regexp = ".*\\S+.*", message = "Text не может состоять из пробелов или быть пустым")
    @Size(min = 20, max = 7000)
    private String adminComment;
}