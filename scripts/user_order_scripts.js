import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    stages: [
        { duration: '30s', target: 35 },
        { duration: '3m', target: 35 },
        { duration: '30s', target: 0 },
    ],
};

const host = 'http://host.docker.internal:8080';

export default function () {
    // --- 0. Setup ---
    const userId = __VU;
    const headers = { 'Content-Type': 'application/json' };

    console.log(`\n------- VU: ${__VU}, Iteration: ${__ITER}, UserID: ${userId} START -------`);

    // --- 1. 인기 상품 조회 ---
    const popularProducts = getPopularProducts(headers);
    if (!popularProducts) { return; }

    // 랜덤 상품 선택
    const randomIndex = Math.floor(Math.random() * popularProducts.length);
    const randomProduct = popularProducts[randomIndex];
    const productId = randomProduct.productId;

    // --- 2. 상품 상세 조회 ---
    const productDetails = getProductDetails(productId, headers);
    if (!productDetails) { return; }

    const optionToOrder = productDetails.productOptions[0];
    const productOptionId = optionToOrder.productOptionId;
    const productPrice = optionToOrder.price;

    // --- 3. 포인트 충전 ---
    chargePoints(userId, productPrice, headers);

    // --- 4. 주문 요청 ---
    const orderId = createOrder(userId, productOptionId, headers);
    if (!orderId) { return; }

    // --- 5. 결제 요청 ---
    requestPayment(orderId, userId, headers);

    // --- 6. 주문 정보 확인 ---
    getOrderInfo(orderId, headers);

    console.log(`------- VU: ${__VU}, Iteration: ${__ITER}, UserID: ${userId} END -------`);
}


/**
 * 1. 인기 상품 목록 조회
 */
function getPopularProducts(headers) {
    const params = {
        headers: headers,
        tags: {
            name: '1. 인기 상품 목록 조회'
        },
    };
    const res = http.get(`${host}/api/products/rank/5`, params);
    const isSuccess = check(res, { '1. Get Popular Products: status 200': (r) => r.status === 200 });

    if (!isSuccess || !res.json('data') || res.json('data').length === 0) {
        console.error('Failed to get popular products.');
        return null;
    }

    console.log('1. Got popular products successfully.');
    sleep(1);
    return res.json('data');
}

/**
 * 2. 특정 상품 상세 정보 조회
 */
function getProductDetails(productId, headers) {
    const params = {
        headers: headers,
        tags: {
            name: '2. 상품 상세 조회'
        },
    };
    const res = http.get(`${host}/api/products/${productId}`, params);

    const isSuccess = check(res, { '2. Get Product Details: status 200': (r) => r.status === 200 });

    if (!isSuccess || !res.json('data.productOptions') || res.json('data.productOptions').length === 0) {
        console.error(`Failed to get details for product ${productId}.`);
        return null;
    }

    console.log(`2. Got details for product ${productId} successfully.`);
    sleep(1);
    return res.json('data');
}

/**
 * 3. 사용자 포인트 충전
 */
function chargePoints(userId, productPrice, headers) {
    const amountToCharge = productPrice * 2; // 상품 2개 구매 가정, 충분히 충전
    const payload = JSON.stringify({ chargePoint: amountToCharge });

    const params = {
        headers: headers,
        tags: {
            name: '3. 포인트 충전'
        },
    };

    const res = http.post(`${host}/api/users/${userId}/points`, payload, params);
    check(res, { '3. Charge Points: status 200': (r) => r.status === 200 });

    console.log(`3. Charged ${amountToCharge} points for user ${userId}.`);
    sleep(1);
}

/**
 * 4. 주문 생성
 */
function createOrder(userId, productOptionId, headers) {
    const payload = JSON.stringify({
        userId: userId,
        orderProductRequests: [{ productOptionId: productOptionId, quantity: 1 }]
    });
    const params = {
        headers: headers,
        tags: {
            name: '4. 주문 요청'
        },
    };

    const res = http.post(`${host}/api/orders`, payload, params);
    const isSuccess = check(res, { '4. Create Order: status 202': (r) => r.status === 202 });

    if (!isSuccess || !res.json('data.id')) {
        console.error('Failed to create order.');
        return null;
    }

    const orderId = res.json('data.id');
    console.log(`4. Created order ${orderId} successfully.`);
    sleep(1);
    return orderId;
}

/**
 * 5. 결제 요청
 */
function requestPayment(orderId, userId, headers) {
    const payload = JSON.stringify({
        orderId: orderId,
        userId: userId,
        type: "POINT"
    });
    const params = {
        headers: headers,
        tags: {
            name: '5. 결제 요청'
        },
    };

    const res = http.post(`${host}/api/payments`, payload, params);
    check(res, { '5. Request Payment: status 200': (r) => r.status === 200 });

    console.log(`5. Payment requested for order ${orderId}.`);
    sleep(1);
}

/**
 * 6. 주문 정보 확인
 */
function getOrderInfo(orderId, headers) {
    const params = {
        headers: headers,
        tags: {
            name: '6. 주문 정보 확인'
        },
    };
    const res = http.get(`${host}/api/orders/${orderId}`, params);
    check(res, { '6. Get Order Info: status 200': (r) => r.status === 200 });

    console.log(`6. Got info for order ${orderId}.`);
    sleep(1);
}

// 실행 명령어
// docker run --rm -i \
// -v ./scripts:/scripts \
// --network e-commerce-java_app-network \
// grafana/k6 run /scripts/user_order_scripts.js --out influxdb=http://influxdb:8086/k6