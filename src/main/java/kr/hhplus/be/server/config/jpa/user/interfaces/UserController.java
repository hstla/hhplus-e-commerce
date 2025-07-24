package kr.hhplus.be.server.config.jpa.user.adapter.in;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.hhplus.be.server.config.jpa.common.CommonResponse;
import kr.hhplus.be.server.config.jpa.user.adapter.in.dto.user.CreateUserRequest;
import kr.hhplus.be.server.config.jpa.user.adapter.in.dto.user.UpdateUserRequest;
import kr.hhplus.be.server.config.jpa.user.adapter.in.dto.user.UserDetailsResponse;
import kr.hhplus.be.server.config.jpa.user.application.port.in.FindUserUseCase;
import kr.hhplus.be.server.config.jpa.user.application.port.in.SignUpUserUseCase;
import kr.hhplus.be.server.config.jpa.user.application.port.in.UpdateUserUseCase;
import kr.hhplus.be.server.config.jpa.user.domain.User;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController implements UserApiSpec{

	private final FindUserUseCase findUserUseCase;
	private final SignUpUserUseCase signUpUseCase;
	private final UpdateUserUseCase updateUserUseCase;

	@Override
	@GetMapping("/{userId}")
	public ResponseEntity<CommonResponse<UserDetailsResponse>> getUserDetails(long userId) {
		User findUser = findUserUseCase.findUser(userId);
		UserDetailsResponse userResponse = UserDetailsResponse.of(findUser);
		return ResponseEntity.ok(CommonResponse.success(userResponse));
	}

	@Override
	@PostMapping
	public ResponseEntity<CommonResponse<UserDetailsResponse>> createUser(CreateUserRequest createUserRequest) {
		User user = signUpUseCase.signUpUser(createUserRequest.name(), createUserRequest.email(), createUserRequest.password());
		return ResponseEntity.ok(CommonResponse.success(UserDetailsResponse.of(user)));
	}

	@Override
	@PostMapping("/{userId}")
	public ResponseEntity<CommonResponse<UserDetailsResponse>> updateUser(long userId, UpdateUserRequest updateUserRequest) {
		User updateUser = updateUserUseCase.updateUser(userId, updateUserRequest.name(), updateUserRequest.email());
		return ResponseEntity.ok(CommonResponse.success(UserDetailsResponse.of(updateUser)));
	}
}