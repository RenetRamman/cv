package com.renet.cvbackend.cv;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.hamcrest.Matchers.contains;

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
class CvControllerTest {

	private final JsonMapper jsonMapper = JsonMapper.builder().build();

	@Autowired
	private MockMvc mockMvc;

	@Test
	void getCv_whenRoleMissing_returnsNotFound() throws Exception {
		mockMvc.perform(get("/api/cv").param("roleProfileSlug", "no-such-role"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("Role profile not found: no-such-role"));
	}

	@Test
	void getCv_ordersSkillsAndExperienceBulletsByRoleWeights() throws Exception {
		createProfile();
		long javaTagId = findOrCreateTag("java", "Java");
		long pythonTagId = findOrCreateTag("python", "Python");
		createRoleProfile("java-role", "Java Role", javaTagId, pythonTagId);

		String categoryName = "CvTest Languages";
		String company = "CvTest Acme";

		mockMvc.perform(post("/api/skill-categories")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "name": "%s",
						  "skills": [
						    { "name": "Python", "tagIds": [%d] },
						    { "name": "Java", "tagIds": [%d] }
						  ]
						}
						""".formatted(categoryName, pythonTagId, javaTagId)))
			.andExpect(status().isCreated());

		mockMvc.perform(post("/api/experiences")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "company": "%s",
						  "title": "Engineer",
						  "location": "Tallinn",
						  "startDate": "2020-01-01",
						  "endDate": null,
						  "bullets": [
						    { "content": "Python scripting", "tagIds": [%d] },
						    { "content": "Java APIs", "tagIds": [%d] }
						  ]
						}
						""".formatted(company, pythonTagId, javaTagId)))
			.andExpect(status().isCreated());

		mockMvc.perform(get("/api/cv").param("roleProfileSlug", "java-role"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.selectedRoleSlug").value("java-role"))
			.andExpect(jsonPath("$.skillCategories[?(@.name=='%s')].skills[0].name".formatted(categoryName))
					.value(contains("Java")))
			.andExpect(jsonPath("$.skillCategories[?(@.name=='%s')].skills[0].highlighted".formatted(categoryName))
					.value(contains(true)))
			.andExpect(jsonPath("$.skillCategories[?(@.name=='%s')].skills[1].name".formatted(categoryName))
					.value(contains("Python")))
			.andExpect(jsonPath("$.experiences[?(@.company=='%s')].bullets[0].content".formatted(company))
					.value(contains("Java APIs")))
			.andExpect(jsonPath("$.experiences[?(@.company=='%s')].bullets[1].content".formatted(company))
					.value(contains("Python scripting")));
	}

	private void createProfile() throws Exception {
		mockMvc.perform(put("/api/profile")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "fullName": "Jane Doe",
						  "headline": "Developer",
						  "email": "jane@example.com",
						  "contactLinks": []
						}
						"""))
			.andExpect(status().is2xxSuccessful());
	}

	private void createRoleProfile(String slug, String name, long highTagId, long lowTagId) throws Exception {
		MvcResult listResult = mockMvc.perform(get("/api/role-profiles"))
			.andExpect(status().isOk())
			.andReturn();
		JsonNode roles = jsonMapper.readTree(listResult.getResponse().getContentAsString());
		for (JsonNode role : roles) {
			if (slug.equals(role.get("slug").asString())) {
				return;
			}
		}

		mockMvc.perform(post("/api/role-profiles")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "slug": "%s",
						  "name": "%s",
						  "type": "PERMANENT",
						  "sortOrder": 10,
						  "tags": [
						    { "tagId": %d, "weight": 1.000 },
						    { "tagId": %d, "weight": 0.100 }
						  ]
						}
						""".formatted(slug, name, highTagId, lowTagId)))
			.andExpect(status().isCreated());
	}

	private long findOrCreateTag(String slug, String name) throws Exception {
		MvcResult listResult = mockMvc.perform(get("/api/tags"))
			.andExpect(status().isOk())
			.andReturn();
		JsonNode tags = jsonMapper.readTree(listResult.getResponse().getContentAsString());
		for (JsonNode tag : tags) {
			if (slug.equals(tag.get("slug").asString())) {
				return tag.get("id").asLong();
			}
		}

		MvcResult result = mockMvc.perform(post("/api/tags")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "slug": "%s",
						  "name": "%s"
						}
						""".formatted(slug, name)))
			.andExpect(status().isCreated())
			.andReturn();
		return jsonMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
	}

}
