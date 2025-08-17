package kr.hhplus.be.api.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import kr.hhplus.be.api.user.controller.dto.user.UserRequest;
import kr.hhplus.be.api.user.controller.dto.user.UserResponse;
import kr.hhplus.be.global.common.CommonResponse;

@Tag(name="유저", description = "유저 관련 API")
public interface UserApiSpec {

	@Operation(summary = "유저 조회", description = "유저 아이디를 입력받아 유저 정보를 반환합니다.")
	ResponseEntity<CommonResponse<UserResponse.UserInfo>> getUserDetails(
		@Parameter(description = "유저 아이디", required = true) @PathVariable @PositiveOrZero long userId);

	@Operation(summary = "유저 생성", description = "유저를 생성합니다.")
	ResponseEntity<CommonResponse<UserResponse.UserInfo>> createUser(
		@Parameter(description = "유저 생성 정보", required = true) @RequestBody @Valid UserRequest.SignUp signUp
	);

	@Operation(summary = "유저 수정", description = "유저 아이다와 정보를 입력받아 유저 정보를 수정합니다.")
	ResponseEntity<CommonResponse<UserResponse.UserInfo>> updateUser(
		@Parameter(description = "유저 아이디", required = true) @PathVariable @PositiveOrZero long userId,
		@Parameter(description = "유저 수정 정보", required = true) @RequestBody @Valid UserRequest.Update updateRequest
	);
}