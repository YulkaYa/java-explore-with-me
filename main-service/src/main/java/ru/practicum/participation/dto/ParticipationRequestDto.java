package ru.practicum.participation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.practicum.participation.ParticipationRequestStatus;
import java.time.LocalDateTime;

@SuperBuilder(toBuilder = true)
@Data
@RequiredArgsConstructor
public class ParticipationRequestDto {
    @NotNull
    private Long id;
    @NotNull
    private Long event;
    @NotNull
    private Long requester;
    @NotNull
    private ParticipationRequestStatus status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created = LocalDateTime.now();
}