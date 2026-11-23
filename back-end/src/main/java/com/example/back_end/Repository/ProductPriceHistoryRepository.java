package com.example.back_end.Repository;

import com.example.back_end.Model.Product;
import java.math.BigDecimal;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class ProductPriceHistoryRepository {
    private final JdbcClient jdbc;

    ProductPriceHistoryRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public void record(Product product, long actorId, BigDecimal previousPrice, String previousCurrency) {
        jdbc.sql("""
                INSERT INTO product_price_history
                    (product_id, actor_user_id, previous_base_price, previous_currency, new_base_price, new_currency)
                VALUES (:productId, :actorId, :previousPrice, :previousCurrency, :price, :currency)
                """)
                .param("productId", product.id())
                .param("actorId", actorId)
                .param("previousPrice", previousPrice)
                .param("previousCurrency", previousCurrency)
                .param("price", product.basePrice())
                .param("currency", product.currency())
                .update();
    }
}
