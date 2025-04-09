package ru.practicum.category.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.category.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByName(String name);

    @Query("SELECT EXISTS (SELECT c.id FROM Category c WHERE c.id = :id)")
    Boolean isCategoryExist(Long id);
}