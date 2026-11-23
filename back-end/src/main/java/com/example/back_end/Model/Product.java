package com.example.back_end.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    private String name;
    private String slug;
    private String description;
    private String allergens;
    private String imageUrl;
    private BigDecimal basePrice;
    private String currency = "USD";
    private boolean active = true;

    protected Product() {}

    public Product(Category category, String name, String slug, BigDecimal basePrice) {
        this.category = category;
        this.name = name;
        this.slug = slug;
        this.basePrice = basePrice;
    }

    public void edit(
            Category category, String name, String slug, String description, String allergens,
            String imageUrl, BigDecimal basePrice, String currency, Boolean active
    ) {
        if (category != null) this.category = category;
        if (name != null) this.name = name;
        if (slug != null) this.slug = slug;
        if (description != null) this.description = emptyToNull(description);
        if (allergens != null) this.allergens = emptyToNull(allergens);
        if (imageUrl != null) this.imageUrl = emptyToNull(imageUrl);
        if (basePrice != null) this.basePrice = basePrice;
        if (currency != null) this.currency = currency;
        if (active != null) this.active = active;
    }

    public void disable() {
        active = false;
    }

    public long id() {
        return id;
    }

    public BigDecimal basePrice() {
        return basePrice;
    }

    public String currency() {
        return currency;
    }

    private static String emptyToNull(String value) {
        return value.isBlank() ? null : value.strip();
    }
}
