package com.renet.cvbackend;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
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

	private static final Set<String> EXPECTED_CONTENT_TABLES = Set.of(
		"profile",
		"contact_link",
		"tag",
		"role_profile",
		"role_profile_tag",
		"introduction",
		"experience",
		"experience_bullet",
		"experience_bullet_tag",
		"education",
		"education_bullet",
		"education_bullet_tag",
		"skill_category",
		"skill",
		"skill_tag",
		"project",
		"project_bullet",
		"project_technology",
		"project_bullet_tag"
	);

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

		assertThat(appliedMigrations)
			.anySatisfy(migration -> {
				assertThat(migration.getVersion().getVersion()).isEqualTo("2");
				assertThat(migration.getDescription()).isEqualTo("cv content schema");
			});
	}

	@Test
	void cvContentTablesExist() throws Exception {
		try (Connection connection = dataSource.getConnection();
				ResultSet tables = connection.getMetaData().getTables(null, "public", "%", new String[] {"TABLE"})) {
			var tableNames = new HashSet<String>();
			while (tables.next()) {
				tableNames.add(tables.getString("TABLE_NAME"));
			}

			assertThat(tableNames).containsAll(EXPECTED_CONTENT_TABLES);
		}
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
