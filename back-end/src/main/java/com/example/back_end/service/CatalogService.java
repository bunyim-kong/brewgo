package com.example.back_end.service;

import com.example.back_end.Model.dto.CategorySummary;
import com.example.back_end.Model.dto.ProductSummary;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CatalogService {
    List<CategorySummary> categories();

    Page<ProductSummary> products(Long branchId, Long categoryId, String query, Pageable page);

    ProductSummary product(String identifier);
}
