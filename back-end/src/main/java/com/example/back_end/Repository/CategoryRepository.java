package com.example.back_end.Repository;

import com.example.back_end.Model.Category;
import com.example.back_end.Model.dto.CategorySummary;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("""
            SELECT new com.example.back_end.Model.dto.CategorySummary(c.id, c.name, c.slug)
            FROM Category c WHERE c.active = true ORDER BY c.sortOrder, c.id
            """)
    List<CategorySummary> findVisibleCategories();
}
