package ru.practicum.location.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@RequiredArgsConstructor
@Embeddable
public class Location {
    @Column(name = "lat")
    @NotNull
    private Double lat;

    @Column(name = "lon")
    @NotNull
    private Double lon;
}