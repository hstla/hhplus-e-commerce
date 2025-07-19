package kr.hhplus.be.server.config.jpa.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
	@Size(min = 2, max = 15) String name,
	@Email String email,
	@Size(min = 5, max = 20) String password
) {
}
