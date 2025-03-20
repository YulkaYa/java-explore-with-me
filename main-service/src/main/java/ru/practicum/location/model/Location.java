package ru.practicum.location.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@RequiredArgsConstructor
@Embeddable
public class Location {
    @NotNull
    private Double lat;
    @NotNull
    private Double lon;
}