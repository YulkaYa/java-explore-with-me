package ru.practicum.category.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.category.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByName(String name);
}