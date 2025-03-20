package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.event.EventState;
import ru.practicum.location.model.Location;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@Builder(toBuilder = true)
public class EventFullDto {
    @NotNull
    private Long id;
    @NotBlank
    @Pattern(regexp = ".*\\S+.*", message = "annotation не может состоять из пробелов или быть пустым")
    private String annotation;
    @NotNull
    private CategoryDto category;
    private Integer confirmedRequests;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime createdOn = LocalDateTime.now();
    @Pattern(regexp = ".*\\S+.*", message = "description не может состоять из пробелов или быть пустым")
    private String description;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime eventDate;
    @NotNull
    private UserShortDto initiator;
    @NotNull
    private Location location;
    @NotNull
    private Boolean paid;
    @Positive
    private Integer participantLimit;
    private LocalDateTime publishedOn;
    private Boolean requestModeration;
    private EventState state;
    @NotBlank
    @Pattern(regexp = ".*\\S+.*", message = "Title не может состоять из пробелов или быть пустым")
    private String title;
    @PositiveOrZero
    private Long views;
}