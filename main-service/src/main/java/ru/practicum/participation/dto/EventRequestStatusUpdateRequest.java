package ru.practicum.participation.dto;

import lombok.Data;
import ru.practicum.participation.ParticipationRequestStatus;

import java.util.List;

@Data
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;
    private ParticipationRequestStatus status;
}