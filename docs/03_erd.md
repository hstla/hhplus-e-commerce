## ✅ Domain Class Diagram

<img width="751" height="441" alt="Image" src="https://github.com/user-attachments/assets/94ac663b-c189-4717-bbaa-71ad09c3d3fe" />

### Aggregate root 분리 이유
user_coupon를 user, payment를 order와 하나의 aggregate로 묶을지 고민했지만, 다음과 같은 이유로 별도의 aggregate로 분리하기로 결정하였습니다.

도메인의 책임이 명확히 분리되어 있음
user_coupon은 쿠폰의 사용 가능 여부 및 상태 관리를 책임지며, payment는 결제 수단, 결제 승인 및 실패와 같은 결제 흐름을 책임집니다. 
이는 user, order 도메인과는 별도로 독립적인 비즈니스 로직을 갖고 있습니다.

라이프사이클이 다름
order는 주문이 생성될 때부터 결제 완료까지의 흐름을 따르지만, payment는 주문이 생성된 이후에도 결제 요청, 승인, 실패, 취소 등 자체적인 라이프사이클을 가져, 
동일한 aggregate로 묶는 것이 오히려 응집도를 해치게 됩니다.

트랜잭션 경계를 작게 가져가기 위함
하나의 order에 payment를 종속되게 하면 주문과 결제 시 트랜잭션의 크기가 너무 커져, 
DB 성능 저하 및 Lock 경합이 발생할 수 있습니다.


## ✅ ERD

<img width="745" height="931" alt="Image" src="https://github.com/user-attachments/assets/caae5de2-caf3-41ab-a76c-28e8b35591f4" />


### 테이블 설명
| 테이블명             | 책임 설명                                             |
|------------------|---------------------------------------------------|
| `user`           | 사용자 정보를 저장 및 보유 포인트 정보를 관리                        |
| `product`        | 상품의 기본 정보(이름, 카테고리, 가격 등)를 관리                     |
| `product_option` | 상품에 대한 세부 옵션(색상, 사이즈 등)과 재고를 관리                   |
| `orders`         | 사용자의 주문 전체를 대표하는 정보와 상태(PENDING, COMPLETED 등)를 저장 |
| `order_product`  | 주문에 포함된 각 상품 옵션의 수량, 금액 등 세부 정보를 관리               |
| `payment`        | 주문에 대한 결제 정보 및 결제 상태를 관리 (성공/실패/환불 등)             |
| `coupon`         | 발급 가능한 쿠폰 정보(할인 방식, 금액, 유효기간 등)를 정의               |
| `coupon_stock`   | 쿠폰의 현재 재고를 정보를 정의(실시간 발급을 위한 쿠폰의 정규화)             |
| `user_coupon`    | 사용자에게 발급된 쿠폰 정보(사용 여부, 만료일 등)를 관리                 |

#### 이후 추가할 테이블
| 테이블명           | 책임 설명                                             |
|----------------|---------------------------------------------------
| `product_rank` | 최근 인기 판매 상품의 순위를 저장 (배치로 집계된 통계 데이터)              |
| `point_history` | 포인트 충전, 차감, 환불 등의 모든 포인트 변화 이력을 기록                |