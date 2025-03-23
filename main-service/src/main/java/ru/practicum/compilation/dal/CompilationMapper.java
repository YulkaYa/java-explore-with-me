package ru.practicum.compilation.dal;

import org.mapstruct.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.model.Compilation;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CompilationMapper {

    Compilation compilationDtotoCompilation(CompilationDto compilationDto);

    CompilationDto compilationToCompilationDto(Compilation compilation);

    List<CompilationDto> toListCompilationDto(List<Compilation> compilations);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", ignore = true)
    Compilation newCompilationDtoToCompilation(NewCompilationDto newCompilationDto);

/* todo удалить?
    @Mapping(source = "updateCompilationRequest.events", target = "events")
    @Mapping(source = "updateCompilationRequest.pinned", target = "pinned")
    @Mapping(source = "updateCompilationRequest.title", target = "title")
    @Mapping(source = "id", target = "id")
    Compilation updateCompilationRequestToCompilation(UpdateCompilationRequest updateCompilationRequest, Long id);
*/

/*    @Mapping(source = "updateCompilationRequest.events", target = "events")
    @Mapping(source = "updateCompilationRequest.pinned", target = "pinned")
    @Mapping(source = "updateCompilationRequest.title", target = "title") todo проверить, что маппинг ниже работает ок*/
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", ignore = true)
    Compilation updateCompilationRequestFromCompilationDto(UpdateCompilationRequest updateCompilationRequest, @MappingTarget Compilation compilation);
}
