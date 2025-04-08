package ru.practicum.compilation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.util.ArrayList;
import java.util.List;

@SuperBuilder(toBuilder = true)
@Data
@RequiredArgsConstructor
public class NewCompilationDto {
    private List<Long> events = new ArrayList<>(); // Список идентификаторов событий
    @NotNull
    private Boolean pinned = false; // Закреплена ли подборка (по умолчанию false)
    @NotBlank
    @Size(min = 1, max = 50)
    private String title; // Заголовок подборки
}