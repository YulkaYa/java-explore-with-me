package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
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
    private String annotation;
    private Long category;
    @Pattern(regexp = ".*\\S+.*", message = "description не может состоять из пробелов или быть пустым")
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Future
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    @Positive
    private Integer participantLimit;
    private Boolean requestModeration;
    @Pattern(regexp = ".*\\S+.*", message = "title не может состоять из пробелов или быть пустым")
    private String title;
}
