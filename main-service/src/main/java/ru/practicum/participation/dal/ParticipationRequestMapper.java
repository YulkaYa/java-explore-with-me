package ru.practicum.participation.dal;

import org.mapstruct.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.model.Event;
import ru.practicum.participation.dto.ParticipationRequestDto;
import ru.practicum.participation.model.ParticipationRequest;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ParticipationRequestMapper {

    @Mapping(source = "event.id", target = "event")
    @Mapping(source = "requester.id", target = "requester")
    ParticipationRequestDto participationRequestToParticipationRequestDto(ParticipationRequest participationRequest);

    List<ParticipationRequestDto> toListParticipationRequestDto(List<ParticipationRequest> participationRequest);

/* todo удалить?
    @Mapping(source = "updateCompilationRequest.events", target = "events")
    @Mapping(source = "updateCompilationRequest.pinned", target = "pinned")
    @Mapping(source = "updateCompilationRequest.title", target = "title")
    @Mapping(source = "id", target = "id")
    Compilation updateCompilationRequestToCompilation(UpdateCompilationRequest updateCompilationRequest, Long id);
*/

/*    @Mapping(source = "updateCompilationRequest.events", target = "events")
    @Mapping(source = "updateCompilationRequest.pinned", target = "pinned")
    @Mapping(source = "updateCompilationRequest.title", target = "title") todo проверить, что маппинг ниже работает ок*//*
    @Mapping(target = "id", ignore = true)
    ParticipationRequest updateParticipationRequestFromParticipationRequestDto(UpdateCompilationRequest updateCompilationRequest, @MappingTarget Compilation compilation);*/
}
