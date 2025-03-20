package ru.practicum.event.model;

import lombok.Data;
import ru.practicum.event.AdminStateAction;
import ru.practicum.location.model.Location;

@Data
public class UpdateEventAdminRequest {
    private String annotation;
    private Long category;
    private String description;
    private String eventDate;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private AdminStateAction stateAction;
    private String title;
}