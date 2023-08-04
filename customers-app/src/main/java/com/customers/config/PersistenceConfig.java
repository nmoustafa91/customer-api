package com.customers.config;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.customers.infrastructure.persistence.audit.AuditorAwareImpl;

@Configuration
@EnableJpaRepositories(basePackages = "com.customers.db.repository")
@EnableJpaAuditing(modifyOnCreate = false, dateTimeProviderRef = "auditingDateTimeProvider")
public class PersistenceConfig {

	@Bean
	public AuditorAware<String> auditorProvider() {
		return new AuditorAwareImpl();
	}

	/**
	 * May not be very useful here in that case, but I need it when I use Postgresql DB.
	 *
	 * @return
	 */
	@Bean(name = "auditingDateTimeProvider")
	public DateTimeProvider dateTimeProvider() {
		return () -> Optional.of(OffsetDateTime.now());
	}
}
