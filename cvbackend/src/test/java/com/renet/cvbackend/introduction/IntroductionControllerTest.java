package com.renet.cvbackend.introduction;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class IntroductionControllerTest {

	private final JsonMapper jsonMapper = JsonMapper.builder().build();

	@Autowired
	private MockMvc mockMvc;

	@Test
	void listIntroductions_whenEmpty_returnsEmptyList() throws Exception {
		mockMvc.perform(get("/api/introductions"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$.length()").value(0));
	}

	@Test
	void createIntroduction_withoutProfile_returnsUnprocessableEntity() throws Exception {
		long roleProfileId = createRoleProfile("general", "General", "GENERAL");

		mockMvc.perform(post("/api/introductions")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "roleProfileId": %d,
						  "content": "Hello"
						}
						""".formatted(roleProfileId)))
			.andExpect(status().isUnprocessableEntity())
			.andExpect(jsonPath("$.message").value("Profile not found"));
	}

	@Test
	void createAndGetIntroduction_returnsIntroduction() throws Exception {
		createProfile();
		long roleProfileId = createRoleProfile("java", "Java Developer", "PERMANENT");

		MvcResult createResult = mockMvc.perform(post("/api/introductions")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "roleProfileId": %d,
						  "content": "Java-focused backend engineer."
						}
						""".formatted(roleProfileId)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").isNumber())
			.andExpect(jsonPath("$.roleProfileId").value(roleProfileId))
			.andExpect(jsonPath("$.roleProfileSlug").value("java"))
			.andExpect(jsonPath("$.roleProfileName").value("Java Developer"))
			.andExpect(jsonPath("$.roleProfileType").value("PERMANENT"))
			.andExpect(jsonPath("$.content").value("Java-focused backend engineer."))
			.andReturn();

		long id = readId(createResult);

		Assertions.assertTrue(
				createResult.getResponse().getHeader("Location").endsWith("/api/introductions/" + id)
		);

		mockMvc.perform(get("/api/introductions/" + id))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").value("Java-focused backend engineer."));

		mockMvc.perform(get("/api/introductions"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(1));
	}

	@Test
	void createIntroduction_withDuplicateRole_returnsConflict() throws Exception {
		createProfile();
		long roleProfileId = createRoleProfile("java", "Java Developer", "PERMANENT");
		createIntroduction(roleProfileId, "First intro");

		mockMvc.perform(post("/api/introductions")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "roleProfileId": %d,
						  "content": "Second intro"
						}
						""".formatted(roleProfileId)))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.message").value("Introduction already exists for role profile: " + roleProfileId));
	}

	@Test
	void createIntroduction_withMissingRoleProfile_returnsUnprocessableEntity() throws Exception {
		createProfile();

		mockMvc.perform(post("/api/introductions")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "roleProfileId": 999999,
						  "content": "Hello"
						}
						"""))
			.andExpect(status().isUnprocessableEntity())
			.andExpect(jsonPath("$.message").value("Role profile not found: 999999"));
	}

	@Test
	void updateIntroduction_changesContentAndRole() throws Exception {
		createProfile();
		long javaRoleId = createRoleProfile("java", "Java Developer", "PERMANENT");
		long backendRoleId = createRoleProfile("backend", "Backend", "PERMANENT");
		long id = createIntroduction(javaRoleId, "Java intro");

		mockMvc.perform(put("/api/introductions/" + id)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "roleProfileId": %d,
						  "content": "Backend intro"
						}
						""".formatted(backendRoleId)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.roleProfileSlug").value("backend"))
			.andExpect(jsonPath("$.content").value("Backend intro"));
	}

	@Test
	void deleteIntroduction_removesIntroduction() throws Exception {
		createProfile();
		long roleProfileId = createRoleProfile("java", "Java Developer", "PERMANENT");
		long id = createIntroduction(roleProfileId, "Java intro");

		mockMvc.perform(delete("/api/introductions/" + id))
			.andExpect(status().isNoContent());

		mockMvc.perform(get("/api/introductions/" + id))
			.andExpect(status().isNotFound());
	}

	@Test
	void getIntroduction_whenMissing_returnsNotFound() throws Exception {
		mockMvc.perform(get("/api/introductions/999999"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("Introduction not found: 999999"));
	}

	private void createProfile() throws Exception {
		mockMvc.perform(put("/api/profile")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "fullName": "Jane Doe",
						  "contactLinks": []
						}
						"""))
			.andExpect(status().isCreated());
	}

	private long createRoleProfile(String slug, String name, String type) throws Exception {
		MvcResult result = mockMvc.perform(post("/api/role-profiles")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "slug": "%s",
						  "name": "%s",
						  "type": "%s",
						  "sortOrder": 0,
						  "tags": []
						}
						""".formatted(slug, name, type)))
			.andExpect(status().isCreated())
			.andReturn();
		return readId(result);
	}

	private long createIntroduction(long roleProfileId, String content) throws Exception {
		MvcResult result = mockMvc.perform(post("/api/introductions")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "roleProfileId": %d,
						  "content": "%s"
						}
						""".formatted(roleProfileId, content)))
			.andExpect(status().isCreated())
			.andReturn();
		return readId(result);
	}

	private long readId(MvcResult result) throws Exception {
		JsonNode json = jsonMapper.readTree(result.getResponse().getContentAsString());
		return json.get("id").asLong();
	}

}
