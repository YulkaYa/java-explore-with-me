package ru.practicum.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.UpdateEventAdminRequest;

import java.util.List;


@RestController
@RequestMapping("/admin/events")
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AdminEventController {
        private final EventService eventService;

        @GetMapping
        public List<EventShortDto> getEvents(@RequestParam(required = false) List<Long> users,
                                     @RequestParam(required = false) List<String> states,
                                     @RequestParam(required = false) List<Long> categories,
                                     @RequestParam(required = false) String rangeStart,
                                     @RequestParam(required = false) String rangeEnd,
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
    }


