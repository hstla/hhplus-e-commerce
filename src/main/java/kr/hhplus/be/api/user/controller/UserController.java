package kr.hhplus.be.api.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.hhplus.be.api.user.controller.dto.user.UserRequest;
import kr.hhplus.be.api.user.controller.dto.user.UserResponse;
import kr.hhplus.be.api.user.usecase.FindUserUseCase;
import kr.hhplus.be.api.user.usecase.SignUpUserUseCase;
import kr.hhplus.be.api.user.usecase.UpdateUserUseCase;
import kr.hhplus.be.api.user.usecase.dto.UserResult;
import kr.hhplus.be.global.common.CommonResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController implements UserApiSpec{

	private final SignUpUserUseCase signUpUserUseCase;
	private final UpdateUserUseCase updateUserUseCase;
	private final FindUserUseCase findUserUseCase;

	@Override
	@GetMapping("/{userId}")
	public ResponseEntity<CommonResponse<UserResponse.UserInfo>> getUserDetails(long userId) {
		UserResult.UserInfo userInfo = findUserUseCase.execute(userId);
		return ResponseEntity.ok(CommonResponse.success(UserResponse.UserInfo.of(userInfo)));
	}

	@Override
	@PostMapping
	public ResponseEntity<CommonResponse<UserResponse.UserInfo>> createUser(UserRequest.SignUp signUp) {
		UserResult.UserInfo createUser = signUpUserUseCase.execute(signUp.toCommand());
		return ResponseEntity.ok(CommonResponse.success(UserResponse.UserInfo.of(createUser)));
	}

	@Override
	@PostMapping("/{userId}")
	public ResponseEntity<CommonResponse<UserResponse.UserInfo>> updateUser(long userId, UserRequest.Update update) {
		UserResult.UserInfo updateUser = updateUserUseCase.execute(userId, update.toCommand());
		return ResponseEntity.ok(CommonResponse.success(UserResponse.UserInfo.of(updateUser)));
	}
}