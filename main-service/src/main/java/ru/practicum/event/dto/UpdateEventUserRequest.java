package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.practicum.event.UserStateAction;

@SuperBuilder(toBuilder = true)
@Data
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateEventUserRequest extends BaseUpdateEventRequest {
    private UserStateAction stateAction;
}