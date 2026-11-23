package com.example.back_end.Model.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProductInput(
        @NotNull(groups = Create.class) @Positive Long categoryId,
        @NotBlank(groups = Create.class) @Size(max = 160) String name,
        @NotNull(groups = Create.class) @Size(max = 180) @Pattern(regexp = "[a-z0-9]+(?:-[a-z0-9]+)*") String slug,
        @Size(max = 16_000) String description,
        @Size(max = 255) String allergens,
        @Size(max = 1024) String imageUrl,
        @NotNull(groups = Create.class) @PositiveOrZero @Digits(integer = 8, fraction = 2) BigDecimal basePrice,
        @Pattern(regexp = "[A-Z]{3}") String currency,
        Boolean active
) {
    public interface Create {}
}
