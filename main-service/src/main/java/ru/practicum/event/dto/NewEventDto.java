package ru.practicum.event.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.location.model.Location;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@Builder(toBuilder = true)
public class NewEventDto {
    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;
    @NotNull
    private Long category;
    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;
    @NotBlank
    @Future
    private LocalDateTime eventDate;
    @NotNull
    private Location location;
    private Boolean paid = false;
    @Positive
    private Integer participantLimit = 0;
    private Boolean requestModeration = true;
    @NotBlank
    @Size(min = 3, max = 120)
    private String title;
}