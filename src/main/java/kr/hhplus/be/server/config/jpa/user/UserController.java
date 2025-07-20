package kr.hhplus.be.server.config.jpa.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.hhplus.be.server.config.jpa.user.dto.CreateUserRequest;
import kr.hhplus.be.server.config.jpa.user.dto.UpdateUserRequest;
import kr.hhplus.be.server.config.jpa.user.dto.UserDetailsResponse;

@RestController
@RequestMapping("/api/users")
public class UserController implements UserApiSpec{

	@Override
	@GetMapping("/{userId}")
	public ResponseEntity<UserDetailsResponse> getUserDetails(long userId) {
		return ResponseEntity.ok(new UserDetailsResponse(1L, "유저 이름", "test@test.com"));
	}

	@Override
	@PostMapping
	public ResponseEntity<UserDetailsResponse> createUser(CreateUserRequest createUserRequest) {
		return ResponseEntity.ok(new UserDetailsResponse(1L, "유저 이름", "test@test.com"));
	}

	@Override
	@PostMapping("/{userId}")
	public ResponseEntity<UserDetailsResponse> updateUser(long userId, UpdateUserRequest updateUserRequest) {
		return ResponseEntity.ok(new UserDetailsResponse(1L, "유저 이름", "test@test.com"));
	}
}