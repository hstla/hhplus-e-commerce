package kr.hhplus.be.server;

import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import lombok.extern.log4j.Log4j2;

@Configuration
@Log4j2
public class TestcontainersConfig {

	public static final MySQLContainer<?> MYSQL_CONTAINER;

	static {
		MYSQL_CONTAINER = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
			.withDatabaseName("hhplus")
			.withUsername("test")
			.withPassword("test")
			.withReuse(true)  // 컨테이너 재사용
			.waitingFor(Wait.forListeningPort())
			.withCommand("--character-set-server=utf8mb4",
				"--collation-server=utf8mb4_unicode_ci",
				"--skip-name-resolve");

		// 컨테이너 시작
		MYSQL_CONTAINER.start();

		// 시스템 프로퍼티 설정
		System.setProperty("spring.datasource.url", MYSQL_CONTAINER.getJdbcUrl());
		System.setProperty("spring.datasource.username", MYSQL_CONTAINER.getUsername());
		System.setProperty("spring.datasource.password", MYSQL_CONTAINER.getPassword());
		System.setProperty("spring.datasource.driver-class-name", "com.mysql.cj.jdbc.Driver");

		// 연결 확인을 위한 로그
		log.info("=== Testcontainer MySQL Started ===");
		log.info("JDBC URL: {}", MYSQL_CONTAINER.getJdbcUrl());
		log.info("Username: {}", MYSQL_CONTAINER.getUsername());
		log.info("================================");
	}
}