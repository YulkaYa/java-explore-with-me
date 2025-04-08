package ru.practicum.category.dal;

import org.mapstruct.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.model.Category;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CategoryMapper {

    Category categoryDtotoCategory(CategoryDto categoryDto);

    @Mapping(target = "id", ignore = true)
    Category newCategoryDtoToCategory(NewCategoryDto newCategoryDto);

    CategoryDto categoryToCategoryDto(Category category);

    @Mapping(target = "id", ignore = true)
    Category updateCategoryFromCategoryDto(CategoryDto categoryDto, @MappingTarget Category category);

    List<CategoryDto> toListCategoryDto(List<Category> categories);
}
