package kr.hhplus.be.global.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import com.zaxxer.hikari.HikariDataSource;

@Configuration(proxyBeanMethods = false)
@EnableJpaAuditing
public class JpaConfig {

	@Bean
	public HikariDataSource hikariDataSource(DataSourceProperties dataSourceProperties) {
		return dataSourceProperties.initializeDataSourceBuilder()
			.type(HikariDataSource.class)
			.build();
	}

	@Bean
	@Primary
	public LazyConnectionDataSourceProxy lazyConnectionDataSourceProxy(HikariDataSource hikariDataSource) {
		return new LazyConnectionDataSourceProxy(hikariDataSource);
	}

	@Primary
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(
		@Qualifier("lazyConnectionDataSourceProxy") DataSource dataSource,
		EntityManagerFactoryBuilder builder
	)  {
		return builder.dataSource(dataSource)
			.packages("kr.hhplus.be")
			.build();
	}
}