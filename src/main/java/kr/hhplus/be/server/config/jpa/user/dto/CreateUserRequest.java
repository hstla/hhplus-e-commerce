package kr.hhplus.be.server.config.jpa.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
	@NotNull @Size(min = 2, max = 15) String name,
	@NotNull @Email String email,
	@NotNull @Size(min = 5, max = 20) String password
) {
}