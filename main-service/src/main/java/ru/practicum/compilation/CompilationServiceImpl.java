package ru.practicum.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.ConditionsNotMetException;
import ru.practicum.common.NotFoundException;
import ru.practicum.compilation.dal.CompilationMapper;
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
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        List<Long> eventIds = newCompilationDto.getEvents();
        validateEventsInCompilationExist(eventIds);
        Compilation compilation = mapper.newCompilationDtoToCompilation(newCompilationDto, getEmptyEventsByIds(eventIds));
        return mapper.compilationToCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    @Transactional
    public void deleteCompilation(Long id) {
        getCompilationByIdOrThrow(id);
        compilationRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long id, UpdateCompilationRequest updateCompilationRequest) {
        List<Long> eventIds = updateCompilationRequest.getEvents();
        validateEventsInCompilationExist(eventIds);
        Compilation compilation =  getCompilationByIdOrThrow(id);

        compilation = mapper.updateCompilationRequestToCompilation(updateCompilationRequest, getEmptyEventsByIds(eventIds), compilation);
        compilation = compilationRepository.save(compilation);
        return mapper.compilationToCompilationDto(compilationRepository.save(compilation));
    }

    private void validateEventsInCompilationExist(List<Long> eventIds) {
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

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        return compilationRepository.findCompilationsByPinned(pinned, page)
                .map(mapper::compilationToCompilationDto)
                .getContent();
    }

    private Compilation getCompilationByIdOrThrow(Long id) {
        return  compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("The required object was not found."));
    }

    @Override
    public CompilationDto getCompilationById(Long id) {
        return mapper.compilationToCompilationDto(getCompilationByIdOrThrow(id));
    }
}
