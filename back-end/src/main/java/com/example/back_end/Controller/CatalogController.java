package com.example.back_end.Controller;

import com.example.back_end.Model.dto.CategorySummary;
import com.example.back_end.Model.dto.ProductSummary;
import com.example.back_end.service.CatalogService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
class CatalogController {
    private final CatalogService catalog;

    CatalogController(CatalogService catalog) {
        this.catalog = catalog;
    }

    @GetMapping("/categories")
    List<CategorySummary> categories() {
        return catalog.categories();
    }

    @GetMapping("/products")
    ProductPage products(
            @RequestParam(required = false) @Positive Long branchId,
            @RequestParam(required = false) @Positive Long categoryId,
            @RequestParam(defaultValue = "") @Size(max = 120) String q,
            @RequestParam(defaultValue = "0") @Min(0) @Max(100_000) int page,
            @RequestParam(defaultValue = "24") @Min(1) @Max(100) int size
    ) {
        var result = catalog.products(branchId, categoryId, q, PageRequest.of(page, size));
        return new ProductPage(result.getContent(), result.getNumber(), result.getTotalPages(), result.getTotalElements());
    }

    @GetMapping("/products/{identifier}")
    ProductSummary product(@PathVariable @Size(max = 180) String identifier) {
        return catalog.product(identifier);
    }

    record ProductPage(List<ProductSummary> items, int page, int totalPages, long totalItems) {}
}
