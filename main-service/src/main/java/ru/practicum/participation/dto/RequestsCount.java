package ru.practicum.participation.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@Data
@RequiredArgsConstructor
public class RequestsCount {
        private Long eventId;
        private Integer count;
}
