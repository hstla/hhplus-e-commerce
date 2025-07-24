package kr.hhplus.be.server.config.jpa.user.application;

import org.springframework.stereotype.Service;

import kr.hhplus.be.server.config.jpa.user.domain.UserInfo;
import kr.hhplus.be.server.config.jpa.user.domain.UserInput;
import kr.hhplus.be.server.config.jpa.user.domain.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserFacade {

	private final UserService userService;

	public UserResult.UserPoint findUserPoint(Long userId) {
		UserInfo.PointInfo findUserPoint = userService.findUserPoint(userId);
		return UserResult.UserPoint.of(findUserPoint.getId(), findUserPoint.getPoint());
	}

	public UserResult.User findUser(Long userId) {
		UserInfo.Info findUser = userService.findUser(userId);
		return UserResult.User.of(findUser);
	}

	public UserResult.User signUpUser(UserCommand.SignUp signUp) {
		UserInfo.Info info = userService.signUpUser(UserInput.SignUp.of(signUp.getName(), signUp.getEmail(), signUp.getPassword()));
		return UserResult.User.of(info);

	}

	public UserResult.User updateUser(long userId, UserCommand.update update) {
		UserInfo.Info info = userService.updateUser(UserInput.Update.of(userId, update.getName(), update.getEmail()));
		return UserResult.User.of(info);
	}

	public UserResult.UserPoint chargeUserPoint(Long userId, Long chargePoint) {
		UserInfo.PointInfo chargeUserPoint = userService.chargeUserPoint(chargePoint, userId);
		return UserResult.UserPoint.of(chargeUserPoint.getId(), chargeUserPoint.getPoint());
	}
}