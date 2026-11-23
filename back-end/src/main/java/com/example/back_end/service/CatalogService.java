package com.example.back_end.service;

import com.example.back_end.Model.dto.AdminProduct;
import com.example.back_end.Model.dto.CategorySummary;
import com.example.back_end.Model.dto.ProductInput;
import com.example.back_end.Model.dto.ProductSummary;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CatalogService {
    List<CategorySummary> categories();

    Page<ProductSummary> products(Long branchId, Long categoryId, String query, Pageable page);

    ProductSummary product(String identifier);

    Page<AdminProduct> adminProducts(Pageable page);

    AdminProduct adminProduct(long id);

    AdminProduct createProduct(ProductInput input);

    AdminProduct updateProduct(long id, ProductInput input, long actorId);

    void deleteProduct(long id);
}
