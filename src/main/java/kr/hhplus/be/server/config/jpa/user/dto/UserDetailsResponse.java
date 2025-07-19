package kr.hhplus.be.server.config.jpa.user.dto;

public record UserDetailsResponse(
	long userId,
	String name,
	String email
) {
}