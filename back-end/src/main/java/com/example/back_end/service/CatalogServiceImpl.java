package com.example.back_end.service;

import com.example.back_end.Model.Category;
import com.example.back_end.Model.Product;
import com.example.back_end.Model.dto.AdminProduct;
import com.example.back_end.Model.dto.CategorySummary;
import com.example.back_end.Model.dto.ProductInput;
import com.example.back_end.Model.dto.ProductSummary;
import com.example.back_end.Repository.CategoryRepository;
import com.example.back_end.Repository.ProductPriceHistoryRepository;
import com.example.back_end.Repository.ProductRepository;
import java.net.URI;
import java.sql.SQLException;
import java.util.Currency;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
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
    private final ProductPriceHistoryRepository priceHistory;

    CatalogServiceImpl(
            BranchService branches, CategoryRepository categories, ProductRepository products,
            ProductPriceHistoryRepository priceHistory
    ) {
        this.branches = branches;
        this.categories = categories;
        this.products = products;
        this.priceHistory = priceHistory;
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
        return product.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Drink is unavailable"));
    }

    @Override
    public Page<AdminProduct> adminProducts(Pageable page) {
        return products.findAdminProducts(page);
    }

    @Override
    public AdminProduct adminProduct(long id) {
        return products.findAdminById(id).orElseThrow(CatalogServiceImpl::productNotFound);
    }

    @Override
    @Transactional
    public AdminProduct createProduct(ProductInput input) {
        validateProduct(input);
        var product = new Product(category(input.categoryId()), input.name().strip(), input.slug(), input.basePrice());
        applyChanges(product, null, input);
        return saveProduct(product);
    }

    @Override
    @Transactional
    public AdminProduct updateProduct(long id, ProductInput input, long actorId) {
        var product = products.findForUpdate(id).orElseThrow(CatalogServiceImpl::productNotFound);
        validateProduct(input);
        var previousPrice = product.basePrice();
        var previousCurrency = product.currency();
        var category = input.categoryId() == null ? null : category(input.categoryId());

        applyChanges(product, category, input);
        var saved = saveProduct(product);
        if (previousPrice.compareTo(saved.basePrice()) != 0 || !previousCurrency.equals(saved.currency())) {
            priceHistory.record(product, actorId, previousPrice, previousCurrency);
        }
        return saved;
    }

    @Override
    @Transactional
    public void deleteProduct(long id) {
        products.findForUpdate(id).orElseThrow(CatalogServiceImpl::productNotFound).disable();
    }

    private Category category(long id) {
        return categories.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT, "Category does not exist"));
    }

    private static void applyChanges(Product product, Category category, ProductInput input) {
        product.edit(category, input.name() == null ? null : input.name().strip(), input.slug(),
                input.description(), input.allergens(), input.imageUrl(), input.basePrice(), input.currency(), input.active());
    }

    private AdminProduct saveProduct(Product product) {
        try {
            var saved = products.saveAndFlush(product);
            return adminProduct(saved.id());
        } catch (DataIntegrityViolationException exception) {
            // MySQL enforces unique slugs, including across concurrent writes.
            if (exception.getMostSpecificCause() instanceof SQLException sql && sql.getErrorCode() == 1062) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Product slug is already in use", exception);
            }
            throw exception;
        }
    }

    private static void validateProduct(ProductInput input) {
        if (input.name() != null && input.name().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT, "Product name is required");
        }
        if (input.currency() != null) {
            try {
                Currency.getInstance(input.currency());
            } catch (IllegalArgumentException exception) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT, "Currency code is invalid");
            }
        }
        if (input.imageUrl() != null && !input.imageUrl().isBlank()) {
            URI image;
            try {
                image = URI.create(input.imageUrl().strip());
            } catch (IllegalArgumentException exception) {
                throw invalidImageUrl();
            }
            var scheme = image.getScheme();
            if (image.getHost() == null || image.getUserInfo() != null
                    || !("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))) {
                throw invalidImageUrl();
            }
        }
    }

    private static ResponseStatusException invalidImageUrl() {
        return new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT, "Image URL must use HTTP or HTTPS");
    }

    private static ResponseStatusException productNotFound() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Product does not exist");
    }
}
