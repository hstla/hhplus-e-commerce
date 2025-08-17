package kr.hhplus.be.api.user.usecase;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.api.user.usecase.dto.UserResult;
import kr.hhplus.be.domain.user.model.User;
import kr.hhplus.be.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FindUserUseCase {

	private final UserRepository userRepository;

	@Transactional(readOnly = true)
	public UserResult.UserInfo execute(Long userId) {
		User findUser = userRepository.findById(userId);
		return UserResult.UserInfo.of(findUser);
	}
}