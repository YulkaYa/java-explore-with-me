package ru.practicum.participation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@Builder(toBuilder = true)
public class ParticipationRequestDto {
    @NotNull
    private Long id;
    @NotNull
    private Long event;
    @NotNull
    private Long requester;
    @NotNull
    private String status;
    @JsonFormat(pattern = "yyyy-MM-ddTHH:mm:ss.SSS")
    private LocalDateTime created = LocalDateTime.now();
}