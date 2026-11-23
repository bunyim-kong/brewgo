CREATE TABLE branches (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL,
    slug VARCHAR(120) NOT NULL UNIQUE,
    timezone VARCHAR(64) NOT NULL,
    address VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    pay_on_pickup BOOLEAN NOT NULL DEFAULT TRUE,
    pickup_lead_minutes SMALLINT UNSIGNED NOT NULL DEFAULT 15,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE branch_hours (
    branch_id BIGINT NOT NULL,
    day_of_week TINYINT NOT NULL,
    opens_at TIME NOT NULL,
    closes_at TIME NOT NULL,
    PRIMARY KEY (branch_id, day_of_week, opens_at),
    FOREIGN KEY (branch_id) REFERENCES branches (id),
    CHECK (day_of_week BETWEEN 1 AND 7),
    CHECK (opens_at >= '00:00:00' AND closes_at <= '24:00:00' AND opens_at < closes_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE cafe_tables (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    branch_id BIGINT NOT NULL,
    label VARCHAR(30) NOT NULL,
    qr_token CHAR(43) CHARACTER SET ascii COLLATE ascii_bin NOT NULL UNIQUE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    UNIQUE (branch_id, label),
    UNIQUE (branch_id, id),
    INDEX ix_tables_branch_active (branch_id, active),
    FOREIGN KEY (branch_id) REFERENCES branches (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    branch_id BIGINT,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(254) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(12) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    FOREIGN KEY (branch_id) REFERENCES branches (id),
    CHECK (role IN ('ADMIN', 'CASHIER', 'BARISTA')),
    CHECK (role = 'ADMIN' OR branch_id IS NOT NULL)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL,
    slug VARCHAR(140) NOT NULL UNIQUE,
    sort_order INT NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_id BIGINT NOT NULL,
    name VARCHAR(160) NOT NULL,
    slug VARCHAR(180) NOT NULL UNIQUE,
    description TEXT,
    allergens VARCHAR(255),
    image_url VARCHAR(1024),
    base_price DECIMAL(10,2) NOT NULL,
    currency CHAR(3) NOT NULL DEFAULT 'USD',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    INDEX ix_products_category_active (category_id, active),
    FOREIGN KEY (category_id) REFERENCES categories (id),
    CHECK (base_price >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE option_groups (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    selection_type VARCHAR(12) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CHECK (selection_type IN ('SINGLE', 'MULTIPLE'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE option_values (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    group_id BIGINT NOT NULL,
    label VARCHAR(100) NOT NULL,
    price_delta DECIMAL(10,2) NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    sort_order INT NOT NULL DEFAULT 0,
    UNIQUE (group_id, id),
    FOREIGN KEY (group_id) REFERENCES option_groups (id),
    CHECK (price_delta >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE product_option_groups (
    product_id BIGINT NOT NULL,
    group_id BIGINT NOT NULL,
    min_select INT NOT NULL DEFAULT 0,
    max_select INT NOT NULL DEFAULT 1,
    sort_order INT NOT NULL DEFAULT 0,
    PRIMARY KEY (product_id, group_id),
    FOREIGN KEY (product_id) REFERENCES products (id),
    FOREIGN KEY (group_id) REFERENCES option_groups (id),
    CHECK (min_select >= 0 AND max_select >= min_select)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE product_option_values (
    product_id BIGINT NOT NULL,
    group_id BIGINT NOT NULL,
    option_value_id BIGINT NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    price_override DECIMAL(10,2),
    PRIMARY KEY (product_id, option_value_id),
    FOREIGN KEY (product_id, group_id) REFERENCES product_option_groups (product_id, group_id),
    FOREIGN KEY (group_id, option_value_id) REFERENCES option_values (group_id, id),
    CHECK (price_override >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
