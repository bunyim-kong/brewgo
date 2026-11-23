package com.example.back_end.Model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;

public record ProductSummary(
        long id, long categoryId, String name, String slug, String description, String allergens,
        String imageUrl, @JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal basePrice, String currency
) {}
