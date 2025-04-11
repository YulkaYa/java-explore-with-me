package ru.practicum.compilation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.practicum.event.dto.EventShortDto;
import java.util.ArrayList;
import java.util.List;

@SuperBuilder(toBuilder = true)
@Data
@RequiredArgsConstructor
public class CompilationDto {
    @NotNull
    private Long id; // Идентификатор подборки
    private List<EventShortDto> events = new ArrayList<>(); // Список событий в подборке
    @NotNull
    private Boolean pinned; // Закреплена ли подборка на главной странице
    @NotBlank
    private String title; // Заголовок подборки
}