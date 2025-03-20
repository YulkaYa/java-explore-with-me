package ru.practicum.compilation.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateCompilationRequest {
    private List<Long> events; // Список идентификаторов событий для обновления
    private Boolean pinned; // Закреплена ли подборка
    @Size(min = 1, max = 50)
    private String title; // Заголовок подборки
}