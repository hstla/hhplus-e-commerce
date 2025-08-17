package kr.hhplus.be.api.user.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.api.user.usecase.dto.UserCommand;
import kr.hhplus.be.api.user.usecase.dto.UserResult;
import kr.hhplus.be.domain.user.model.User;
import kr.hhplus.be.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UpdateUserUseCase {

	private final UserRepository userRepository;

	@Transactional
	public UserResult.UserInfo execute(Long userId, UserCommand.Update updateRequest) {
		User findUser = userRepository.findById(userId);

		findUser.updateName(updateRequest.name());
		User savedUser = userRepository.save(findUser);

		return UserResult.UserInfo.of(savedUser);
	}
}