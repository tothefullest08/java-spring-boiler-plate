-- Food Delivery System Database Schema
-- 음식 주문 시스템 데이터베이스 스키마

-- Shop Context 테이블들
CREATE TABLE shop (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    name VARCHAR(255) NOT NULL,
    min_order_amount DECIMAL(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE shop_operating_hours (
    shop_id VARCHAR(36) NOT NULL,
    day_of_week TINYINT NOT NULL, -- 1=Monday, 7=Sunday
    open_time TIME,
    close_time TIME,
    PRIMARY KEY (shop_id, day_of_week),
    FOREIGN KEY (shop_id) REFERENCES shop(id) ON DELETE CASCADE
);

CREATE TABLE menu (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    shop_id VARCHAR(36) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    base_price DECIMAL(10,2),
    is_open BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (shop_id) REFERENCES shop(id) ON DELETE CASCADE
);

CREATE TABLE option_group (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    menu_id VARCHAR(36) NOT NULL,
    name VARCHAR(255) NOT NULL,
    is_required BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (menu_id) REFERENCES menu(id) ON DELETE CASCADE,
    UNIQUE KEY unique_option_group_name_per_menu (menu_id, name)
);

CREATE TABLE option (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    option_group_id VARCHAR(36) NOT NULL,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10,2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (option_group_id) REFERENCES option_group(id) ON DELETE CASCADE
);

-- Order Context 테이블들
CREATE TABLE cart (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    user_id VARCHAR(36) NOT NULL,
    shop_id VARCHAR(36),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE cart_line_item (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    cart_id VARCHAR(36) NOT NULL,
    menu_id VARCHAR(36) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cart_id) REFERENCES cart(id) ON DELETE CASCADE
);

CREATE TABLE cart_item_selected_option (
    cart_line_item_id VARCHAR(36) NOT NULL,
    option_id VARCHAR(36) NOT NULL,
    PRIMARY KEY (cart_line_item_id, option_id),
    FOREIGN KEY (cart_line_item_id) REFERENCES cart_line_item(id) ON DELETE CASCADE
);

CREATE TABLE order_table (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    user_id VARCHAR(36) NOT NULL,
    shop_id VARCHAR(36) NOT NULL,
    total_price DECIMAL(10,2),
    order_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE order_line_item (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    order_id VARCHAR(36) NOT NULL,
    menu_id VARCHAR(36) NOT NULL,
    menu_name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    line_price DECIMAL(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES order_table(id) ON DELETE CASCADE
);

CREATE TABLE order_item_selected_option (
    order_line_item_id VARCHAR(36) NOT NULL,
    option_id VARCHAR(36) NOT NULL,
    option_name VARCHAR(255) NOT NULL,
    option_price DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (order_line_item_id, option_id),
    FOREIGN KEY (order_line_item_id) REFERENCES order_line_item(id) ON DELETE CASCADE
);

-- User Context 테이블들
CREATE TABLE user (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 인덱스 생성
CREATE INDEX idx_menu_shop_id ON menu(shop_id);
CREATE INDEX idx_menu_is_open ON menu(is_open);
CREATE INDEX idx_option_group_menu_id ON option_group(menu_id);
CREATE INDEX idx_option_option_group_id ON option(option_group_id);
CREATE INDEX idx_cart_user_id ON cart(user_id);
CREATE INDEX idx_cart_line_item_cart_id ON cart_line_item(cart_id);
CREATE INDEX idx_order_user_id ON order_table(user_id);
CREATE INDEX idx_order_line_item_order_id ON order_line_item(order_id);

-- 샘플 데이터 삽입 (테스트용)
INSERT INTO shop (id, name, min_order_amount) VALUES 
('shop-001', '맛있는 한식당', 15000.00),
('shop-002', '피자헤븐', 20000.00);

INSERT INTO user (id, name, email) VALUES 
('user-001', '김철수', 'kim@example.com'),
('user-002', '이영희', 'lee@example.com');