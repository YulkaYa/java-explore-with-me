package ru.practicum.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;

@SuperBuilder(toBuilder = true)
@Data
@RequiredArgsConstructor
public class CommentShortDto {
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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserShortDto creator;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;
}
