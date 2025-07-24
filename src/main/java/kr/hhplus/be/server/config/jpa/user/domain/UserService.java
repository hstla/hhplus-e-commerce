package kr.hhplus.be.server.config.jpa.user.domain;

import org.springframework.stereotype.Service;

import kr.hhplus.be.server.config.jpa.error.RestApiException;
import kr.hhplus.be.server.config.jpa.error.UserErrorCode;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	public UserInfo.Info findUser(Long userId) {
		return UserInfo.Info.of(getUser(userId));
	}

	public void validateExist(Long userId) {
		if (!userRepository.existsById(userId)) {
			throw new RestApiException(UserErrorCode.INACTIVE_USER);
		}
	}

	public UserInfo.Info signUpUser(UserInput.SignUp signUp) {
		if (userRepository.existsByEmail(signUp.getEmail())) {
			throw new RestApiException(UserErrorCode.DUPLICATE_EMAIL);
		}
		User user = User.SignUpUser(signUp.getName(), signUp.getEmail(), signUp.getPassword());
		userRepository.save(user);
		return UserInfo.Info.of(user);
	}

	public UserInfo.Info updateUser(UserInput.Update update) {
		User findUser = getUser(update.getId());
		findUser.updateNameAndEmail(update.getName(), update.getEmail());
		User saveUser = userRepository.save(findUser);
		return UserInfo.Info.of(saveUser);
	}

	public UserInfo.PointInfo chargeUserPoint(Long userId, Long pointAmount) {
		User findUser = getUser(userId);
		findUser.chargePoint(pointAmount);
		User save = userRepository.save(findUser);
		return UserInfo.PointInfo.of(save);
	}

	public UserInfo.PointInfo findUserPoint(Long userId) {
		User user = getUser(userId);
		return UserInfo.PointInfo.of(user);
	}

	private User getUser(Long userId) {
		return userRepository.findById(userId).orElseThrow(() -> new RestApiException(UserErrorCode.INACTIVE_USER));
	}

	public void payUserPoint(Long userId, Long totalPrice) {
		getUser(userId).usePoint(totalPrice);
	}
}