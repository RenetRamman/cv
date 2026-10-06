package com.renet.cvbackend.skill;

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
class SkillCategoryControllerTest {

	private final JsonMapper jsonMapper = JsonMapper.builder().build();

	@Autowired
	private MockMvc mockMvc;

	@Test
	void listSkillCategories_whenEmpty_returnsEmptyList() throws Exception {
		mockMvc.perform(get("/api/skill-categories"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$.length()").value(0));
	}

	@Test
	void createSkillCategory_withoutProfile_returnsUnprocessableEntity() throws Exception {
		mockMvc.perform(post("/api/skill-categories")
				.contentType(MediaType.APPLICATION_JSON)
				.content(sampleBackendCategoryJson()))
			.andExpect(status().isUnprocessableEntity())
			.andExpect(jsonPath("$.message").value("Profile not found"));
	}

	@Test
	void createAndGetSkillCategory_returnsCategoryWithSkillIds() throws Exception {
		createProfile();

		MvcResult createResult = mockMvc.perform(post("/api/skill-categories")
				.contentType(MediaType.APPLICATION_JSON)
				.content(sampleBackendCategoryJson()))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").isNumber())
			.andExpect(jsonPath("$.name").value("Backend"))
			.andExpect(jsonPath("$.skills.length()").value(3))
			.andExpect(jsonPath("$.skills[0].id").isNumber())
			.andExpect(jsonPath("$.skills[0].name").value("Java"))
			.andExpect(jsonPath("$.skills[1].name").value("Spring Boot"))
			.andExpect(jsonPath("$.skills[2].name").value("Python"))
			.andReturn();

		long id = readId(createResult);

		Assertions.assertTrue(
				createResult.getResponse().getHeader("Location").endsWith("/api/skill-categories/" + id)
		);

		mockMvc.perform(get("/api/skill-categories/" + id))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value("Backend"))
			.andExpect(jsonPath("$.skills[0].id").isNumber());

		mockMvc.perform(get("/api/skill-categories"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(1));
	}

	@Test
	void updateSkillCategory_replacesSkills() throws Exception {
		createProfile();
		long id = createBackendCategory();

		mockMvc.perform(put("/api/skill-categories/" + id)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "name": "Frontend",
						  "skills": [
						    { "name": "React" },
						    { "name": "TypeScript" }
						  ]
						}
						"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value("Frontend"))
			.andExpect(jsonPath("$.skills.length()").value(2))
			.andExpect(jsonPath("$.skills[0].id").isNumber())
			.andExpect(jsonPath("$.skills[0].name").value("React"))
			.andExpect(jsonPath("$.skills[1].name").value("TypeScript"));
	}

	@Test
	void deleteSkillCategory_removesCategory() throws Exception {
		createProfile();
		long id = createBackendCategory();

		mockMvc.perform(delete("/api/skill-categories/" + id))
			.andExpect(status().isNoContent());

		mockMvc.perform(get("/api/skill-categories/" + id))
			.andExpect(status().isNotFound());

		mockMvc.perform(get("/api/skill-categories"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(0));
	}

	@Test
	void getSkillCategory_whenMissing_returnsNotFound() throws Exception {
		mockMvc.perform(get("/api/skill-categories/999999"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("Skill category not found: 999999"));
	}

	@Test
	void createSkillCategory_withInvalidRequest_returnsBadRequest() throws Exception {
		createProfile();

		mockMvc.perform(post("/api/skill-categories")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "name": "",
						  "skills": [
						    { "name": "" }
						  ]
						}
						"""))
			.andExpect(status().isBadRequest());
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

	private long createBackendCategory() throws Exception {
		MvcResult result = mockMvc.perform(post("/api/skill-categories")
				.contentType(MediaType.APPLICATION_JSON)
				.content(sampleBackendCategoryJson()))
			.andExpect(status().isCreated())
			.andReturn();
		return readId(result);
	}

	private String sampleBackendCategoryJson() {
		return """
				{
				  "name": "Backend",
				  "skills": [
				    { "name": "Java" },
				    { "name": "Spring Boot" },
				    { "name": "Python" }
				  ]
				}
				""";
	}

	private long readId(MvcResult result) throws Exception {
		JsonNode json = jsonMapper.readTree(result.getResponse().getContentAsString());
		return json.get("id").asLong();
	}

}
