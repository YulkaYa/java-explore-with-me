package ru.practicum.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.NotFoundException;
import ru.practicum.compilation.dal.CompilationMapper;
import ru.practicum.compilation.dal.CompilationRepository;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.model.Compilation;

import java.beans.Transient;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper mapper;

    @Override
    @Transactional
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = mapper.newCompilationDtoToCompilation(newCompilationDto);
        return mapper.compilationToCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    @Transactional
    public void deleteCompilation(Long id) {
        getCompilationById(id);
        compilationRepository.deleteById(id);
    }


    @Override
    @Transactional
    public CompilationDto updateCompilation(Long id, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation =  mapper.compilationDtotoCompilation(getCompilationById(id));
        compilation = mapper.updateCompilationRequestFromCompilationDto(updateCompilationRequest, compilation);
        return mapper.compilationToCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        return compilationRepository.findByPinned(pinned, page)
                .map(mapper::compilationToCompilationDto)
                .getContent();
        //return mapper.toListCompilationDto(compilationRepository.findAll()); todo
    }

    @Override
    public CompilationDto getCompilationById(Long id) {
        return mapper.compilationToCompilationDto(compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("The required object was not found.")));
    }
}
