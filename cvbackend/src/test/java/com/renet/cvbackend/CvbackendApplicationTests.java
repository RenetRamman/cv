package com.renet.cvbackend;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.util.Arrays;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jpa.autoconfigure.JpaProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class CvbackendApplicationTests {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private Flyway flyway;

	@Autowired
	private JpaProperties jpaProperties;

	@Test
	void contextLoads() {
	}

	@Test
	void flywayMigrationsAreApplied() {
		var appliedMigrations = Arrays.stream(flyway.info().applied()).toList();

		assertThat(appliedMigrations)
			.anySatisfy(migration -> {
				assertThat(migration.getVersion().getVersion()).isEqualTo("1");
				assertThat(migration.getDescription()).isEqualTo("initial schema");
			});
	}

	@Test
	void openInViewIsDisabled() {
		assertThat(jpaProperties.getOpenInView()).isFalse();
	}

	@Test
	void canConnectToPostgreSQL() throws Exception {
		try (Connection connection = dataSource.getConnection()) {
			assertThat(connection.isValid(5)).isTrue();
			assertThat(connection.getMetaData().getDatabaseProductName()).isEqualTo("PostgreSQL");
		}
	}

}
