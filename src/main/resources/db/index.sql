-- 인덱스 삭제 명령어
# DROP INDEX idx_user_coupon ON user_coupon;
# DROP INDEX idx_product_id ON product_option;
# DROP INDEX idx_order_id ON order_product;
# ALTER TABLE user DROP INDEX UK_user_email;

-- user_coupon 테이블에 user_id를 기준으로 인덱스 생성
CREATE INDEX idx_user_coupon ON user_coupon (user_id, coupon_id);

-- product_option 테이블에 product_id를 기준으로 인덱스 생성
CREATE INDEX idx_product_id ON product_option (product_id);

-- order_product 테이블에 order_id를 기준으로 인덱스 생성
CREATE INDEX idx_order_id ON order_product (order_id);

-- user 테이블에 email 컬럼을 기준으로 UNIQUE 인덱스 생성
CREATE UNIQUE INDEX UK_user_email ON user (email);

-- 인덱스 보기
SHOW INDEX FROM user;
SHOW INDEX FROM user_coupon;
SHOW INDEX FROM product_option;
SHOW INDEX FROM order_product;

CREATE TABLE `orders` (
                          `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '주문 ID',
                          `user_id` BIGINT NOT NULL COMMENT '사용자 ID',
                          `user_coupon_id` BIGINT NULL COMMENT '사용자 쿠폰 ID',
                          `original_price` BIGINT NOT NULL COMMENT '원래 가격',
                          `discount_price` BIGINT NOT NULL COMMENT '할인된 가격',
                          `total_price` BIGINT NOT NULL COMMENT '최종 결제 가격',
                          `status` VARCHAR(255) NOT NULL COMMENT '주문 상태',
                          `order_at` DATETIME(6) NOT NULL COMMENT '주문 시간',
                          `created_at` DATETIME(6) NOT NULL COMMENT '생성 시간',
                          `updated_at` DATETIME(6) NOT NULL COMMENT '수정 시간',
                          PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `order_product` (
                                 `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '주문 상품 ID',
                                 `order_id` BIGINT NOT NULL COMMENT '주문 ID',
                                 `product_option_id` BIGINT NOT NULL COMMENT '상품 옵션 ID',
                                 `product_name` VARCHAR(30) NOT NULL COMMENT '상품 이름 (스냅샷)',
                                 `product_stock` INT NOT NULL COMMENT '상품 수량 (스냅샷)',
                                 `product_price` BIGINT NOT NULL COMMENT '상품 가격 (스냅샷)',
                                 `created_at` DATETIME(6) NOT NULL COMMENT '생성 시간',
                                 `updated_at` DATETIME(6) NOT NULL COMMENT '수정 시간',
                                 PRIMARY KEY (`id`),
                                 KEY idx_order_id (`order_id`)
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO orders (
    user_id,
    user_coupon_id,
    original_price,
    discount_price,
    total_price,
    status,
    order_at,
    created_at,
    updated_at
) VALUES (
             1,                     -- user_id (임의 유저 ID)
             NULL,                  -- 쿠폰은 없음
             10000,                 -- 원래 가격
             2000,                  -- 할인 금액
             8000,                  -- 최종 결제 금액
             'COMPLETED',           -- 주문 상태
             DATE_SUB(NOW(), INTERVAL 1 DAY), -- 주문 시간 (어제)
             DATE_SUB(NOW(), INTERVAL 1 DAY), -- 생성 시간 (어제)
             DATE_SUB(NOW(), INTERVAL 1 DAY)  -- 수정 시간 (어제)
         );

INSERT INTO order_product (
    order_id, product_option_id, product_name, product_stock, product_price, created_at, updated_at
) VALUES
      (509, 3,  '상품A', 10, 15000, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
      (509, 5,  '상품A1', 12, 15000, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
      (509, 7,  '상품B', 11, 32000, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
      (509, 12, '상품C', 5, 7800,  DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
      (509, 9,  '상품D', 27, 45000, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
      (509, 14, '상품E', 9, 22000, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY));


SELECT
    p.id,
    p.name,
    p.description,
    p.category,
    SUM(op.product_stock) AS total_stock
FROM order_product op
         INNER JOIN product_option po ON op.product_option_id = po.id
         INNER JOIN product p ON po.product_id = p.id
         INNER JOIN orders o ON o.id = op.order_id
WHERE o.order_at BETWEEN '2025-09-20 00:00:00' AND '2025-09-23 23:59:59'
  AND o.status IN ('AWAITING_PAYMENT', 'COMPLETED')
GROUP BY p.id, p.name, p.description, p.category
ORDER BY total_stock DESC;


EXPLAIN select p1_0.id,
       p1_0.category,
       p1_0.created_at,
       p1_0.description,
       p1_0.name,
       p1_0.updated_at
from product p1_0
where p1_0.id=1;

EXPLAIN
select po1_0.id,
       po1_0.created_at,
       po1_0.name,
       po1_0.price,
       po1_0.product_id,
       po1_0.stock,
       po1_0.updated_at
from product_option po1_0
where po1_0.product_id=1;