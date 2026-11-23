package com.example.back_end.Controller;

import com.example.back_end.Model.dto.AdminProduct;
import com.example.back_end.Model.dto.ProductInput;
import com.example.back_end.auth.StaffPrincipal;
import com.example.back_end.service.CatalogService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.groups.Default;
import java.net.URI;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/products")
class AdminProductController {
    private final CatalogService catalog;

    AdminProductController(CatalogService catalog) {
        this.catalog = catalog;
    }

    @GetMapping
    ProductPage products(
            @RequestParam(defaultValue = "0") @Min(0) @Max(100_000) int page,
            @RequestParam(defaultValue = "24") @Min(1) @Max(100) int size
    ) {
        var result = catalog.adminProducts(PageRequest.of(page, size));
        return new ProductPage(result.getContent(), result.getNumber(), result.getTotalPages(), result.getTotalElements());
    }

    @GetMapping("/{id}")
    AdminProduct product(@PathVariable @Positive long id) {
        return catalog.adminProduct(id);
    }

    @PostMapping
    ResponseEntity<AdminProduct> create(@RequestBody @Validated({Default.class, ProductInput.Create.class}) ProductInput input) {
        var product = catalog.createProduct(input);
        return ResponseEntity.created(URI.create("/api/admin/products/" + product.id())).body(product);
    }

    @PatchMapping("/{id}")
    AdminProduct update(
            @PathVariable @Positive long id, @Valid @RequestBody ProductInput input, @AuthenticationPrincipal StaffPrincipal staff
    ) {
        return catalog.updateProduct(id, input, staff.id());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable @Positive long id) {
        catalog.deleteProduct(id);
    }

    record ProductPage(List<AdminProduct> items, int page, int totalPages, long totalItems) {}
}
