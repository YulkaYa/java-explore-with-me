package ru.practicum.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dal.CategoryMapper;
import ru.practicum.category.dal.CategoryRepository;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.model.Category;
import ru.practicum.common.ConditionsNotMetException;
import ru.practicum.common.DuplicatedDataException;
import ru.practicum.common.NotFoundException;
import ru.practicum.event.dal.EventRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper mapper;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        Category category = mapper.newCategoryDtoToCategory(newCategoryDto);
        validateNewNameCategory(null, newCategoryDto.getName());
        return mapper.categoryToCategoryDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.isCategoryExist(id)) {
            throw new NotFoundException("Category not found");
        }
        if (eventRepository.findIdsByCategoryId(id, PageRequest.of(0, 1)).getContent().isEmpty()) {
            categoryRepository.deleteById(id);
        } else throw new ConditionsNotMetException("For the requested operation the conditions are not met.");
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
        Category category = mapper.categoryDtotoCategory(getCategoryById(id));
        String nameInNewCategory = categoryDto.getName();
        validateNewNameCategory(id, nameInNewCategory);
        category = mapper.updateCategoryFromCategoryDto(categoryDto, category);
        return mapper.categoryToCategoryDto(categoryRepository.save(category));
    }

    private void validateNewNameCategory(Long id, String nameInNewCategory) {
        Category categoryWithSameName = categoryRepository.findByName(nameInNewCategory);
        if (nameInNewCategory != null && categoryWithSameName != null) {
            if (nameInNewCategory.equals(categoryWithSameName.getName())
                    && (id == null || !id.equals(categoryWithSameName.getId()))) {
                throw new DuplicatedDataException("Integrity constraint has been violated.");
            }
        }
    }

    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        return categoryRepository.findAll(page)
                .map(mapper::categoryToCategoryDto)
                .getContent();
    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Category not found"));
        return mapper.categoryToCategoryDto(category);
    }
}
