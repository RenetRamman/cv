package com.renet.cvbackend.experience;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
class ExperienceControllerTest {

	private final JsonMapper jsonMapper = JsonMapper.builder().build();

	@Autowired
	private MockMvc mockMvc;

	@Test
	void listExperiences_whenEmpty_returnsEmptyList() throws Exception {
		mockMvc.perform(get("/api/experiences"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$.length()").value(0));
	}

	@Test
	void createExperience_withoutProfile_returnsUnprocessableEntity() throws Exception {
		mockMvc.perform(post("/api/experiences")
				.contentType(MediaType.APPLICATION_JSON)
				.content(sampleExperienceJson("Acme", "Engineer")))
			.andExpect(status().isUnprocessableEntity())
			.andExpect(jsonPath("$.message").value("Profile not found"));
	}

	@Test
	void createAndGetExperience_returnsExperienceWithBulletIds() throws Exception {
		createProfile();

		MvcResult createResult = mockMvc.perform(post("/api/experiences")
				.contentType(MediaType.APPLICATION_JSON)
				.content(sampleExperienceJson("Acme", "Software Engineer")))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").isNumber())
			.andExpect(jsonPath("$.company").value("Acme"))
			.andExpect(jsonPath("$.title").value("Software Engineer"))
			.andExpect(jsonPath("$.location").value("Tallinn"))
			.andExpect(jsonPath("$.startDate").value("2020-01-01"))
			.andExpect(jsonPath("$.endDate").value(nullValue()))
			.andExpect(jsonPath("$.bullets.length()").value(1))
			.andExpect(jsonPath("$.bullets[0].id").isNumber())
			.andExpect(jsonPath("$.bullets[0].content").value("Built REST APIs"))
			.andReturn();

		long id = readId(createResult);

		org.junit.jupiter.api.Assertions.assertTrue(
				createResult.getResponse().getHeader("Location").endsWith("/api/experiences/" + id)
		);

		mockMvc.perform(get("/api/experiences/" + id))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.company").value("Acme"))
			.andExpect(jsonPath("$.bullets[0].id").isNumber());

		mockMvc.perform(get("/api/experiences"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(1));
	}

	@Test
	void updateExperience_replacesBullets() throws Exception {
		createProfile();
		long id = createExperience("Acme", "Engineer");

		mockMvc.perform(put("/api/experiences/" + id)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "company": "Acme Corp",
						  "title": "Senior Engineer",
						  "location": "Tartu",
						  "startDate": "2020-01-01",
						  "endDate": "2023-06-30",
						  "bullets": [
						    { "content": "Led backend work" },
						    { "content": "Mentored juniors" }
						  ]
						}
						"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.company").value("Acme Corp"))
			.andExpect(jsonPath("$.title").value("Senior Engineer"))
			.andExpect(jsonPath("$.endDate").value("2023-06-30"))
			.andExpect(jsonPath("$.bullets.length()").value(2))
			.andExpect(jsonPath("$.bullets[0].id").isNumber())
			.andExpect(jsonPath("$.bullets[0].content").value("Led backend work"));
	}

	@Test
	void deleteExperience_removesExperience() throws Exception {
		createProfile();
		long id = createExperience("Acme", "Engineer");

		mockMvc.perform(delete("/api/experiences/" + id))
			.andExpect(status().isNoContent());

		mockMvc.perform(get("/api/experiences/" + id))
			.andExpect(status().isNotFound());

		mockMvc.perform(get("/api/experiences"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(0));
	}

	@Test
	void getExperience_whenMissing_returnsNotFound() throws Exception {
		mockMvc.perform(get("/api/experiences/999999"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("Experience not found: 999999"));
	}

	@Test
	void createExperience_withInvalidDates_returnsBadRequest() throws Exception {
		createProfile();

		mockMvc.perform(post("/api/experiences")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "company": "Acme",
						  "title": "Engineer",
						  "startDate": "2023-01-01",
						  "endDate": "2020-01-01",
						  "bullets": []
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

	private long createExperience(String company, String title) throws Exception {
		MvcResult result = mockMvc.perform(post("/api/experiences")
				.contentType(MediaType.APPLICATION_JSON)
				.content(sampleExperienceJson(company, title)))
			.andExpect(status().isCreated())
			.andReturn();
		return readId(result);
	}

	private String sampleExperienceJson(String company, String title) {
		return """
				{
				  "company": "%s",
				  "title": "%s",
				  "location": "Tallinn",
				  "startDate": "2020-01-01",
				  "endDate": null,
				  "bullets": [
				    { "content": "Built REST APIs" }
				  ]
				}
				""".formatted(company, title);
	}

	private long readId(MvcResult result) throws Exception {
		JsonNode json = jsonMapper.readTree(result.getResponse().getContentAsString());
		return json.get("id").asLong();
	}

}
