package com.example.back_end.Repository;

import com.example.back_end.Model.Product;
import com.example.back_end.Model.dto.AdminProduct;
import com.example.back_end.Model.dto.ProductSummary;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {
    String VISIBLE_PRODUCTS = """
            SELECT new com.example.back_end.Model.dto.ProductSummary(
                p.id, c.id, p.name, p.slug, p.description, p.allergens, p.imageUrl, p.basePrice, p.currency
            )
            FROM Product p JOIN p.category c
            WHERE p.active = true AND c.active = true
            """;

    @Query(VISIBLE_PRODUCTS + """
                AND (:categoryId IS NULL OR c.id = :categoryId)
                AND (:query = '' OR locate(lower(:query), lower(p.name)) > 0)
            ORDER BY c.sortOrder, p.name, p.id
            """)
    Page<ProductSummary> findVisibleProducts(Long categoryId, String query, Pageable page);

    @Query(VISIBLE_PRODUCTS + " AND p.slug = :slug")
    Optional<ProductSummary> findVisibleBySlug(String slug);

    @Query(VISIBLE_PRODUCTS + " AND p.id = :id")
    Optional<ProductSummary> findVisibleById(long id);

    String ADMIN_PRODUCTS = """
            SELECT new com.example.back_end.Model.dto.AdminProduct(
                p.id, p.category.id, p.name, p.slug, p.description, p.allergens, p.imageUrl,
                p.basePrice, p.currency, p.active
            )
            FROM Product p
            """;

    @Query(ADMIN_PRODUCTS + " ORDER BY p.name, p.id")
    Page<AdminProduct> findAdminProducts(Pageable page);

    @Query(ADMIN_PRODUCTS + " WHERE p.id = :id")
    Optional<AdminProduct> findAdminById(long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findForUpdate(long id);
}
