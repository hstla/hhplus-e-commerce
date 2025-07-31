package kr.hhplus.be.server.config.jpa.user.domain;

import kr.hhplus.be.server.config.jpa.error.RestApiException;
import kr.hhplus.be.server.config.jpa.error.UserErrorCode;
import kr.hhplus.be.server.config.jpa.user.domain.Point;

public class User {

	private Long id; // 도메인 모델의 식별자. JPA 어노테이션 없음.
	private String name;
	private String email;
	private String password;
	private Point point; // Long -> Point VO 타입으로 변경

	// 도메인 모델 생성자: ID는 새로 생성될 때는 null, 조회될 때는 값이 주입됨.
	public User(Long id, String name, String email, String password, Point point) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.password = password;
		this.point = point;
	}

	// --- 비즈니스 로직을 위한 정적 팩토리 메서드 ---
	// User 생성 시 유효성 검증과 초기값 설정 담당
	public static User signUpUser(String name, String email, String password) {
		validateName(name);
		validateEmail(email);
		validatePassword(password);
		return new User(null, name, email, password, Point.zero()); // 신규 User는 ID가 아직 없음, 초기 포인트는 Point.zero()로 설정
	}

	// --- 비즈니스 로직을 위한 메서드 ---
	public void updateNameAndEmail(String name, String email) {
		// 비즈니스 규칙: 이름/이메일 변경 시 유효성 검증 필요하면 추가
		this.name = name;
		this.email = email;
	}

	public void chargePoint(Long amount) { // 인자명 변경 (pointAmount -> amount)
		this.point = this.point.charge(amount); // Point VO의 charge 메서드 호출
	}

	public void usePoint(Long totalPrice) {
		this.point = this.point.use(totalPrice); // Point VO의 use 메서드 호출
	}

	// --- 내부 유효성 검증 메서드 (비즈니스 로직의 일부) ---
	private static void validateName(String name) {
		if (name == null || name.length() < 2 || name.length() > 10) {
			throw new RestApiException(UserErrorCode.INVALID_USER_NAME);
		}
	}

	private static void validateEmail(String email) {
		if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
			throw new RestApiException(UserErrorCode.INVALID_USER_EMAIL);
		}
	}

	private static void validatePassword(String password) {
		if (password == null || password.length() < 5 || password.length() > 20) {
			throw new RestApiException(UserErrorCode.INVALID_USER_PASSWORD);
		}
	}

	// --- Getter 메서드 ---
	public Long getId() { return id; }
	public String getName() { return name; }
	public String getEmail() { return email; }
	public String getPassword() { return password; }
	public Point getPoint() { return point; } // Point VO를 반환

	// --- Repository에서 ID를 주입하기 위한 Setter (제한된 접근 수준) ---
	// 이 Setter는 도메인 모델이 DB에서 로드되거나 저장된 후 ID를 할당받을 때만 사용됩니다.
	// 일반적인 비즈니스 로직에서는 직접 호출되지 않습니다.
	public void setId(Long id) {
		this.id = id;
	}
}