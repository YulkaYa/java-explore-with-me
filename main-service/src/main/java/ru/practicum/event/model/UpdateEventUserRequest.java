package ru.practicum.event.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.practicum.event.UserStateAction;
import ru.practicum.location.model.Location;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateEventUserRequest {
    @Pattern(regexp = ".*\\S+.*", message = "annotation не может состоять из пробелов или быть пустым")
    private String annotation;
    private Long category;
    @Pattern(regexp = ".*\\S+.*", message = "description не может состоять из пробелов или быть пустым")
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    @Positive
    private Integer participantLimit;
    private Boolean requestModeration;
    private UserStateAction stateAction;
    @Pattern(regexp = ".*\\S+.*", message = "title не может состоять из пробелов или быть пустым")
    private String title;
}