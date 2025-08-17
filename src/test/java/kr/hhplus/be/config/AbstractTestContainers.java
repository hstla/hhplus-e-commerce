package kr.hhplus.be.config;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

public abstract class AbstractTestContainers {

	// MySQL
	protected static final MySQLContainer<?> MYSQL_CONTAINER =
		new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
			.withDatabaseName("hhplus")
			.withUsername("test")
			.withPassword("test")
			.withReuse(true) // 한번만 띄워서 재사용
			.withCommand("--character-set-server=utf8mb4",
				"--collation-server=utf8mb4_unicode_ci",
				"--skip-name-resolve",
				"--default-authentication-plugin=mysql_native_password");

	// Redis
	protected static final GenericContainer<?> REDIS_CONTAINER =
		new GenericContainer<>(DockerImageName.parse("redis:7.0.11"))
			.withExposedPorts(6379);

	static {
		MYSQL_CONTAINER.start();
		REDIS_CONTAINER.start();
	}

	@DynamicPropertySource
	static void overrideProps(DynamicPropertyRegistry registry) {
		// MySQL
		registry.add("spring.datasource.url", MYSQL_CONTAINER::getJdbcUrl);
		registry.add("spring.datasource.username", MYSQL_CONTAINER::getUsername);
		registry.add("spring.datasource.password", MYSQL_CONTAINER::getPassword);
		registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");

		// Redis
		registry.add("spring.redis.host", REDIS_CONTAINER::getHost);
		registry.add("spring.redis.port", () -> REDIS_CONTAINER.getFirstMappedPort().toString());
	}
}