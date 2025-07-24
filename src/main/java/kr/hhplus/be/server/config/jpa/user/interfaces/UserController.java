package kr.hhplus.be.server.config.jpa.user.interfaces;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.hhplus.be.server.config.jpa.common.CommonResponse;
import kr.hhplus.be.server.config.jpa.user.application.UserFacade;
import kr.hhplus.be.server.config.jpa.user.application.UserResult;
import kr.hhplus.be.server.config.jpa.user.interfaces.dto.user.UserRequest;
import kr.hhplus.be.server.config.jpa.user.interfaces.dto.user.UserResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController implements UserApiSpec{

	private final UserFacade userFacade;

	@Override
	@GetMapping("/{userId}")
	public ResponseEntity<CommonResponse<UserResponse.User>> getUserDetails(long userId) {
		UserResult.User findUser = userFacade.findUser(userId);
		return ResponseEntity.ok(CommonResponse.success(UserResponse.User.of(findUser)));
	}

	@Override
	@PostMapping
	public ResponseEntity<CommonResponse<UserResponse.User>> createUser(UserRequest.SignUp signUp) {
		UserResult.User createUser = userFacade.signUpUser(signUp.toCommand());
		return ResponseEntity.ok(CommonResponse.success(UserResponse.User.of(createUser)));
	}

	@Override
	@PostMapping("/{userId}")
	public ResponseEntity<CommonResponse<UserResponse.User>> updateUser(long userId, UserRequest.Update update) {
		UserResult.User updateUser = userFacade.updateUser(userId, update.toCommand());
		return ResponseEntity.ok(CommonResponse.success(UserResponse.User.of(updateUser)));
	}
}