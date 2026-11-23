CREATE TABLE product_price_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    actor_user_id BIGINT NOT NULL,
    previous_base_price DECIMAL(10,2) NOT NULL,
    previous_currency CHAR(3) NOT NULL,
    new_base_price DECIMAL(10,2) NOT NULL,
    new_currency CHAR(3) NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    INDEX ix_product_price_history (product_id, created_at),
    FOREIGN KEY (product_id) REFERENCES products (id),
    FOREIGN KEY (actor_user_id) REFERENCES users (id),
    CHECK (previous_base_price >= 0 AND new_base_price >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
