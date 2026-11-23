CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    public_code VARCHAR(24) CHARACTER SET ascii COLLATE ascii_bin NOT NULL UNIQUE,
    tracking_token_hash BINARY(32) NOT NULL,
    branch_id BIGINT NOT NULL,
    table_id BIGINT,
    type VARCHAR(10) NOT NULL,
    status VARCHAR(20) NOT NULL,
    payment_policy VARCHAR(16) NOT NULL,
    desired_pickup_at DATETIME(6),
    confirmed_pickup_at DATETIME(6),
    currency CHAR(3) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    adjustments DECIMAL(10,2) NOT NULL DEFAULT 0,
    total DECIMAL(10,2) NOT NULL,
    guest_name VARCHAR(120),
    notes VARCHAR(500),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    version BIGINT NOT NULL DEFAULT 0,
    INDEX ix_orders_queue (branch_id, status, created_at),
    INDEX ix_orders_pickup (branch_id, type, desired_pickup_at),
    FOREIGN KEY (branch_id) REFERENCES branches (id),
    FOREIGN KEY (branch_id, table_id) REFERENCES cafe_tables (branch_id, id),
    CHECK (status IN ('PENDING_PAYMENT', 'CONFIRMED', 'PREPARING', 'READY', 'COMPLETED', 'CANCELLED')),
    CHECK (subtotal >= 0 AND adjustments >= 0 AND total = subtotal + adjustments),
    CHECK (
        (type = 'DINE_IN' AND table_id IS NOT NULL AND desired_pickup_at IS NULL
            AND confirmed_pickup_at IS NULL AND payment_policy = 'PAY_AT_CASHIER')
        OR
        (type = 'PICKUP' AND table_id IS NULL AND desired_pickup_at IS NOT NULL
            AND payment_policy = 'PAY_ON_PICKUP')
    )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name_snapshot VARCHAR(160) NOT NULL,
    quantity INT NOT NULL,
    unit_base_price DECIMAL(10,2) NOT NULL,
    unit_options_total DECIMAL(10,2) NOT NULL,
    line_total DECIMAL(10,2) NOT NULL,
    special_instructions VARCHAR(500),
    FOREIGN KEY (order_id) REFERENCES orders (id),
    FOREIGN KEY (product_id) REFERENCES products (id),
    CHECK (quantity > 0 AND unit_base_price >= 0 AND unit_options_total >= 0),
    CHECK (line_total = quantity * (unit_base_price + unit_options_total))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE order_item_options (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_item_id BIGINT NOT NULL,
    option_value_id BIGINT,
    group_name_snapshot VARCHAR(100) NOT NULL,
    value_name_snapshot VARCHAR(100) NOT NULL,
    price_delta_snapshot DECIMAL(10,2) NOT NULL,
    UNIQUE (order_item_id, option_value_id),
    FOREIGN KEY (order_item_id) REFERENCES order_items (id),
    FOREIGN KEY (option_value_id) REFERENCES option_values (id),
    CHECK (price_delta_snapshot >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE payments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    method VARCHAR(16) NOT NULL,
    status VARCHAR(10) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    currency CHAR(3) NOT NULL,
    verified_by_user_id BIGINT,
    paid_at DATETIME(6),
    reference VARCHAR(120),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    INDEX ix_payments_order_status (order_id, status),
    FOREIGN KEY (order_id) REFERENCES orders (id),
    FOREIGN KEY (verified_by_user_id) REFERENCES users (id),
    CHECK (method IN ('CASH', 'PAY_ON_PICKUP', 'ONLINE_FUTURE')),
    CHECK (status IN ('PENDING', 'PAID', 'FAILED', 'REFUNDED')),
    CHECK (status NOT IN ('PAID', 'REFUNDED') OR (paid_at IS NOT NULL AND verified_by_user_id IS NOT NULL)),
    CHECK (amount >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE order_status_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    from_status VARCHAR(20),
    to_status VARCHAR(20) NOT NULL,
    actor_user_id BIGINT,
    reason VARCHAR(500),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    INDEX ix_order_history (order_id, created_at),
    FOREIGN KEY (order_id) REFERENCES orders (id),
    FOREIGN KEY (actor_user_id) REFERENCES users (id),
    CHECK (to_status IN ('PENDING_PAYMENT', 'CONFIRMED', 'PREPARING', 'READY', 'COMPLETED', 'CANCELLED')),
    CHECK (from_status IN ('PENDING_PAYMENT', 'CONFIRMED', 'PREPARING', 'READY', 'COMPLETED', 'CANCELLED')),
    CHECK (to_status <> 'CANCELLED' OR NULLIF(TRIM(reason), '') IS NOT NULL)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE idempotency_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    scope VARCHAR(60) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    key_hash BINARY(32) NOT NULL,
    request_hash BINARY(32) NOT NULL,
    response_summary JSON,
    order_id BIGINT NOT NULL,
    expires_at DATETIME(6) NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    UNIQUE (scope, key_hash),
    INDEX ix_idempotency_expiry (expires_at),
    FOREIGN KEY (order_id) REFERENCES orders (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
