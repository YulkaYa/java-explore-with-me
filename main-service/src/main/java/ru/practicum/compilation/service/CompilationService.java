package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    CompilationDto add(NewCompilationDto newCompilationDto);

    void delete(Long id);

    CompilationDto update(Long id, UpdateCompilationRequest updateCompilationRequest);

    List<CompilationDto> getPinned(Boolean pinned, int from, int size);

    CompilationDto getById(Long id);
}
