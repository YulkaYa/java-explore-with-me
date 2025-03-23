package ru.practicum.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.UpdateEventAdminRequest;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/events")
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserEventController {
        private final EventService eventService;

        @GetMapping
        public List<EventFullDto> getEvents(@RequestParam(required = false) List<Long> users,
                                             @RequestParam(required = false) List<EventState> states,
                                             @RequestParam(required = false) List<Long> categories,
                                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                             @RequestParam(defaultValue = "0") int from,
                                             @RequestParam(defaultValue = "10") int size) {
            return eventService.getEvents(users, states, categories, rangeStart, rangeEnd, from, size);
        }

        @PatchMapping("/{eventId}")
        @Validated
        public EventFullDto updateEvent(@PathVariable Long eventId,
                                        @RequestBody @Valid UpdateEventAdminRequest updateEventAdminRequest) {
            return eventService.updateEvent(null, eventId, updateEventAdminRequest, 1);
        }


    @GetMapping
    public List<EventShortDto> getEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(required = false) Boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {

        return eventService.getPublishedEvents(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }
    }


