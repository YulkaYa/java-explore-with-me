package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.ConditionsNotMetException;
import ru.practicum.common.NotFoundException;
import ru.practicum.compilation.dal.CompilationRepository;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.dal.EventRepository;
import ru.practicum.event.model.Event;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper mapper;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto add(NewCompilationDto newCompilationDto) {
        List<Long> eventIds = newCompilationDto.getEvents();
        validateEventsExist(eventIds);
        List<Event> events = eventRepository.findAllWithCategoriesByEventIds(eventIds);

        Compilation compilation = mapper.newDtoToEntity(newCompilationDto, events);
        return mapper.toDto(compilationRepository.save(compilation));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!compilationRepository.existsById(id)) {
            throw new NotFoundException("The required object was not found.");
        }
        compilationRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CompilationDto update(Long id, UpdateCompilationRequest updateCompilationRequest) {
        List<Long> eventIds = updateCompilationRequest.getEvents();
        validateEventsExist(eventIds);
        Compilation compilation =  getByIdOrThrow(id);

        compilation = mapper.updateEntity(updateCompilationRequest, getEmptyEventsByIds(eventIds), compilation);
        compilation = compilationRepository.save(compilation);
        return mapper.toDto(compilationRepository.save(compilation));
    }

    @Override
    public List<CompilationDto> getPinned(Boolean pinned, int from, int size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        Page<Compilation> compilationPage = compilationRepository.findCompilationsByPinned(pinned, page);
        return compilationPage
                .map(mapper::toDto)
                .getContent();
    }

    @Override
    public CompilationDto getById(Long id) {
        return mapper.toDto(getByIdOrThrow(id));
    }

    private void validateEventsExist(List<Long> eventIds) {
        if (eventRepository.findIdsByEventId(eventIds).size() != eventIds.size()) {
            throw new ConditionsNotMetException("Events not found");
        }
    }

    private List<Event> getEmptyEventsByIds(List<Long> eventIds) {
        List<Event> eventsToUpdate = new ArrayList<>();
        for (Long eventId : eventIds) {
            Event event = new Event();
            event.setId(eventId);
            eventsToUpdate.add(event);
        }
        return eventsToUpdate;
    }

    private Compilation getByIdOrThrow(Long id) {
        return  compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("The required object was not found."));
    }
}
