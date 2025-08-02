package kr.hhplus.be.server.config.jpa.api.user.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.config.jpa.api.user.usecase.dto.UserCommand;
import kr.hhplus.be.server.config.jpa.api.user.usecase.dto.UserResult;
import kr.hhplus.be.server.config.jpa.user.domain.model.User;
import kr.hhplus.be.server.config.jpa.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UpdateUserUseCase {

	private final UserRepository userRepository;

	@Transactional
	public UserResult.UserInfo execute(Long userId, UserCommand.Update updateRequest) {
		User findUser = userRepository.findById(userId);

		findUser.updateName(updateRequest.getName());
		User savedUser = userRepository.save(findUser);

		return UserResult.UserInfo.of(savedUser);
	}
}