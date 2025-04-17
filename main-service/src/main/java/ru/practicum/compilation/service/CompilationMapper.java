package ru.practicum.compilation.service;

import org.mapstruct.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.service.EventServiceImpl;
import ru.practicum.event.service.EventMapper;
import ru.practicum.event.model.Event;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = { EventMapper.class, EventServiceImpl.class })
public interface CompilationMapper {

    CompilationDto toDto(Compilation compilation);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", source = "events")
    Compilation newDtoToEntity(NewCompilationDto newCompilationDto, List<Event> events);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", source = "events")
    Compilation updateEntity(UpdateCompilationRequest updateCompilationRequest, List<Event> events, @MappingTarget Compilation compilation);

}
