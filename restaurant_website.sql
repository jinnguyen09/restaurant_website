-- 1. Bảng restaurants (Chi nhánh nhà hàng)
CREATE TABLE restaurants (
    restaurant_id SMALLINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    slogan VARCHAR(255),                  -- Khẩu hiệu của quán
    restaurant_avatar VARCHAR(255),       -- Ảnh đại diện/Logo
    restaurant_img VARCHAR(255),          -- Ảnh bìa/Trang chủ
    address TEXT NOT NULL,
    phone VARCHAR(15),
    email VARCHAR(100),                   -- Email liên hệ
    opening_time TIME,
    closing_time TIME,
    capacity INT UNSIGNED DEFAULT 0,      -- Sức chứa khách
    description TEXT,                     -- Mô tả chi tiết quán
	map_url TEXT,
    status TINYINT UNSIGNED DEFAULT 1,    -- 1: Hoạt động, 0: Ngừng
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Bảng ranked (Hạng thành viên)
CREATE TABLE ranked (
    rank_id SMALLINT UNSIGNED PRIMARY KEY,
    rank_name VARCHAR(100) NOT NULL,
    min_points INT UNSIGNED NOT NULL DEFAULT 0,
    discount_percent DECIMAL(5, 2) NOT NULL DEFAULT 0.00
);

-- 3. Bảng users (Bảng người dùng)
CREATE TABLE users (
    user_id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    rank_id SMALLINT UNSIGNED,
    avatar VARCHAR(255),
    create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    email VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(15),
    points int,
    
    -- Thiết lập khóa ngoại liên kết với bảng ranked
    CONSTRAINT fk_user_rank FOREIGN KEY (rank_id) 
    REFERENCES ranked(rank_id) 
    ON DELETE SET NULL 
    ON UPDATE CASCADE
);

-- 4. Bảng roles (Bảng vai trò)
CREATE TABLE roles (
    role_id SMALLINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- 5. Bảng users_roles (Bảng phân quyền)
CREATE TABLE users_roles (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT UNSIGNED NOT NULL,
    role_id SMALLINT UNSIGNED NOT NULL,
    restaurant_id SMALLINT UNSIGNED NOT NULL,
        
    -- Khóa ngoại liên kết đến bảng users
    CONSTRAINT fk_ur_user FOREIGN KEY (user_id) 
    REFERENCES users(user_id) ON DELETE CASCADE,
    
    -- Khóa ngoại liên kết đến bảng roles
    CONSTRAINT fk_ur_role FOREIGN KEY (role_id) 
    REFERENCES roles(role_id) ON DELETE CASCADE,
    
	-- Khóa ngoại liên kết đến bảng restaurants
    CONSTRAINT fk_ur_restaurant FOREIGN KEY (restaurant_id) 
    REFERENCES restaurants(restaurant_id) ON DELETE CASCADE
);

-- 6. Bảng point (Lịch sử điểm)
CREATE TABLE point (
    point_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    order_id BIGINT UNSIGNED DEFAULT NULL, -- Liên kết với bảng orders nếu có
    amount INT NOT NULL,
    type VARCHAR(20) DEFAULT 'earn', -- earn hoặc spend
    source_type VARCHAR(50) DEFAULT 'register', -- register, order, admin...
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_point_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 7. Bảng notifications (Bảng thông báo)
CREATE TABLE notifications (
    notification_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    status VARCHAR(20) DEFAULT 'active',
    link_url VARCHAR(255) DEFAULT NULL,
    create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_noti_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 8. Bảng categories (Danh mục)
CREATE TABLE categories (
    category_id SMALLINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL,
    description TEXT,
    parent_id SMALLINT UNSIGNED,
    CONSTRAINT fk_category_parent FOREIGN KEY (parent_id) 
        REFERENCES categories(category_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. Bảng products (Sản phẩm gốc)
CREATE TABLE products (
    product_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    category_id SMALLINT UNSIGNED,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50),
    unit VARCHAR(50), -- Ví dụ: Cái, Bát, Đĩa, kg...
    preparation_time SMALLINT UNSIGNED COMMENT 'Thời gian chuẩn bị tính bằng phút',
    image_url VARCHAR(500),
    description TEXT,
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) 
        REFERENCES categories(category_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10. Bảng restaurant_products (Sản phẩm theo nhà hàng)
CREATE TABLE restaurant_products (
    restaurant_product_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    restaurant_id SMALLINT UNSIGNED NOT NULL, 
    product_id INT UNSIGNED NOT NULL,
    price DECIMAL(12, 2) NOT NULL,
    is_available BOOLEAN DEFAULT TRUE,
    stock_quantity INT DEFAULT 0,
    is_featured BOOLEAN DEFAULT FALSE,
    
    CONSTRAINT fk_rp_product FOREIGN KEY (product_id) 
        REFERENCES products(product_id) ON DELETE CASCADE,
    -- CONSTRAINT fk_rp_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id)
    
    CONSTRAINT fk_restaurant_product_restaurant
		FOREIGN KEY (restaurant_id) 
		REFERENCES restaurants(restaurant_id)
		ON DELETE CASCADE
		ON UPDATE CASCADE
        
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 11. Bảng favourites (Bảng món ăn yêu thích)
CREATE TABLE favourites (
    favourite_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    product_id INT UNSIGNED NOT NULL,
    
    -- Đảm bảo một người dùng không lưu trùng một sản phẩm nhiều lần vào danh sách yêu thích
    UNIQUE KEY unique_user_favourite (user_id, product_id),
    
    -- Khóa ngoại liên kết tới bảng người dùng (Thay 'users' bằng tên bảng thực tế của bạn)
    CONSTRAINT fk_fav_user FOREIGN KEY (user_id) 
        REFERENCES users(user_id) ON DELETE CASCADE,
        
    -- Khóa ngoại liên kết tới bảng sản phẩm
    CONSTRAINT fk_fav_product FOREIGN KEY (product_id) 
        REFERENCES products(product_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 12. Tạo bảng discounts (Lưu thông tin chương trình khuyến mãi)
CREATE TABLE `discounts` (
    `discount_id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `restaurant_id` SMALLINT UNSIGNED NOT NULL,
    `discount_name` VARCHAR(255) NOT NULL,
    `discount_type` ENUM('PERCENTAGE', 'FIXED_AMOUNT') NOT NULL COMMENT 'Loại: Phần trăm hoặc Số tiền cố định',
    `discount_value` DECIMAL(12, 2) NOT NULL COMMENT 'Giá trị giảm',
    `start_date` DATETIME NOT NULL,
    `end_date` DATETIME NOT NULL,
    `status` TINYINT DEFAULT 1 COMMENT '1: Hoạt động, 0: Tạm ngưng',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_discounts_restaurant 
		FOREIGN KEY (restaurant_id) REFERENCES restaurants(restaurant_id)
		ON DELETE CASCADE

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 13. Tạo bảng product_discounts (Bảng trung gian áp dụng cho từng món của từng chi nhánh)
CREATE TABLE `product_discounts` (
    `product_discounts_id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `restaurant_product_id` INT UNSIGNED NOT NULL,
    `discount_id` INT UNSIGNED NOT NULL,
    `special_price` DECIMAL(12, 2) DEFAULT NULL COMMENT 'Giá sau khi đã áp dụng giảm giá',
    
    -- Ràng buộc khóa ngoại
    CONSTRAINT `fk_pd_restaurant_product` 
        FOREIGN KEY (`restaurant_product_id`) 
        REFERENCES `restaurant_products` (`restaurant_product_id`) 
        ON DELETE CASCADE,
        
    CONSTRAINT `fk_pd_discount` 
        FOREIGN KEY (`discount_id`) 
        REFERENCES `discounts` (`discount_id`) 
        ON DELETE CASCADE
        
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 14. Tạo bảng reservation (Bảng đặt bàn)
CREATE TABLE reservation (
    reservation_id INT UNSIGNED AUTO_INCREMENT,
    user_id BIGINT UNSIGNED NOT NULL,
    restaurant_id SMALLINT UNSIGNED NOT NULL,
    reservation_time DATETIME NOT NULL,
    number_of_people INT NOT NULL,
    status VARCHAR(50),
    description TEXT,
    
    PRIMARY KEY (resvervation_id),
    
	CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(user_id),
	CONSTRAINT fk_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(restaurant_id)
);

