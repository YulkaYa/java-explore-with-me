package ru.practicum.category.service;

import org.mapstruct.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.model.Category;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CategoryMapper {

    Category toEntity(CategoryDto categoryDto);

    @Mapping(target = "id", ignore = true)
    Category newDtoToEntity(NewCategoryDto newCategoryDto);

    CategoryDto toDto(Category category);

    @Mapping(target = "id", ignore = true)
    Category updateFromDto(CategoryDto categoryDto, @MappingTarget Category category);

    List<CategoryDto> toListOfDto(List<Category> categories);
}
