package com.example.back_end.service;

import com.example.back_end.Model.dto.CategorySummary;
import com.example.back_end.Model.dto.ProductSummary;
import com.example.back_end.Repository.CategoryRepository;
import com.example.back_end.Repository.ProductRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
class CatalogServiceImpl implements CatalogService {
    private final BranchService branches;
    private final CategoryRepository categories;
    private final ProductRepository products;

    CatalogServiceImpl(BranchService branches, CategoryRepository categories, ProductRepository products) {
        this.branches = branches;
        this.categories = categories;
        this.products = products;
    }

    @Override
    public List<CategorySummary> categories() {
        return categories.findVisibleCategories();
    }

    @Override
    public Page<ProductSummary> products(Long branchId, Long categoryId, String query, Pageable page) {
        if (branchId != null && !branches.isActive(branchId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Branch is unavailable");
        }
        return products.findVisibleProducts(categoryId, query.strip(), page);
    }

    @Override
    public ProductSummary product(String identifier) {
        var product = products.findVisibleBySlug(identifier);
        if (product.isEmpty() && identifier.matches("[0-9]{1,19}")) {
            try {
                product = products.findVisibleById(Long.parseLong(identifier));
            } catch (NumberFormatException ignored) {
                // The numeric identifier is outside the database ID range.
            }
        }
        return product.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product is unavailable"));
    }
}
