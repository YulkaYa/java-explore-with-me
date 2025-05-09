package ru.practicum.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.service.EventService;
import ru.practicum.event.model.EventState;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/admin/events")
@Validated
@RequiredArgsConstructor
public class AdminEventController {
    @Autowired
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
            return eventService.update(null, eventId, updateEventAdminRequest, 1);
        }
    }


