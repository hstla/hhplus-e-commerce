INSERT INTO `product` (`name`, `category`, `description`, `created_at`, `updated_at`) VALUES
  ('무선 블루투스 이어폰', 'DIGITAL', '노이즈 캔슬링 지원 무선 이어폰', NOW(), NOW()),
  ('프리미엄 원두 커피', 'FOOD', '콜롬비아산 아라비카 100% 원두 커피', NOW(), NOW()),
  ('코튼 오버핏 티셔츠', 'CLOTHING', '편안한 착용감과 캐주얼한 디자인의 오버핏 티셔츠', NOW(), NOW()),
  ('나이키 러닝화', 'SHOES', '통기성과 쿠션감을 갖춘 경량 러닝화', NOW(), NOW()),
  ('유기농 그린티 파우더', 'FOOD', '스무디나 베이킹에 활용 가능한 말차 가루', NOW(), NOW()
);


INSERT INTO `product_option` (`product_id`, `name`, `price`, `stock`, `created_at`, `updated_at`) VALUES
-- 1. 무선 블루투스 이어폰
(1, '화이트', 89000, 100, NOW(), NOW()),
(1, '블랙', 89000, 120, NOW(), NOW()),
(1, '레드', 92000, 80, NOW(), NOW()),

-- 2. 프리미엄 원두 커피
(2, '200g', 15000, 200, NOW(), NOW()),
(2, '500g', 35000, 150, NOW(), NOW()),
(2, '1kg', 65000, 100, NOW(), NOW()),

-- 3. 코튼 오버핏 티셔츠
(3, 'M 사이즈', 25000, 300, NOW(), NOW()),
(3, 'L 사이즈', 25000, 250, NOW(), NOW()),
(3, 'XL 사이즈', 25000, 200, NOW(), NOW()),

-- 4. 나이키 러닝화
(4, '260mm', 99000, 100, NOW(), NOW()),
(4, '270mm', 99000, 120, NOW(), NOW()),
(4, '280mm', 99000, 90, NOW(), NOW()),

-- 5. 유기농 그린티 파우더
(5, '100g', 12000, 180, NOW(), NOW()),
(5, '200g', 22000, 150, NOW(), NOW()),
(5, '500g', 50000, 80, NOW(), NOW());

INSERT INTO `user` (name, email, password, point, created_at, updated_at) VALUES
('유저테스트이름', 'test11@email.com', 'password', 0, NOW(), NOW()),
('유저테스트이름', 'test12@email.com', 'password', 0, NOW(), NOW());

