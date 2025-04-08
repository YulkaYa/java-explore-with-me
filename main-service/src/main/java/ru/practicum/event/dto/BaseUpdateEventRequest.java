package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.practicum.location.model.Location;

import java.time.LocalDateTime;

@SuperBuilder(toBuilder = true)
@Data
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseUpdateEventRequest {
    @Pattern(regexp = ".*\\S+.*", message = "annotation не может состоять из пробелов или быть пустым")
    @Size(min = 20, max = 2000)
    private String annotation;
    private Long category;
    @Pattern(regexp = "(\\n*.*\\r*.*)*(.*\\S+.*)*(\\n*.*\\r*.*)*", message = "description не может состоять из пробелов или быть пустым")
    @Size(min = 20, max = 7000)
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Future
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    @PositiveOrZero
    private Integer participantLimit;
    private Boolean requestModeration;
    @Pattern(regexp = ".*\\S+.*", message = "title не может состоять из пробелов или быть пустым")
    @Size(min = 3, max = 120)
    private String title;
}
