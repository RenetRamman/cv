package com.renet.cvbackend.education;

import static org.hamcrest.Matchers.nullValue;
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
class EducationControllerTest {

	private final JsonMapper jsonMapper = JsonMapper.builder().build();

	@Autowired
	private MockMvc mockMvc;

	@Test
	void listEducations_whenEmpty_returnsEmptyList() throws Exception {
		mockMvc.perform(get("/api/educations"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$.length()").value(0));
	}

	@Test
	void createEducation_withoutProfile_returnsUnprocessableEntity() throws Exception {
		mockMvc.perform(post("/api/educations")
				.contentType(MediaType.APPLICATION_JSON)
				.content(sampleEducationJson()))
			.andExpect(status().isUnprocessableEntity())
			.andExpect(jsonPath("$.message").value("Profile not found"));
	}

	@Test
	void createAndGetEducation_returnsEducationWithBulletIds() throws Exception {
		createProfile();

		MvcResult createResult = mockMvc.perform(post("/api/educations")
				.contentType(MediaType.APPLICATION_JSON)
				.content(sampleEducationJson()))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").isNumber())
			.andExpect(jsonPath("$.institution").value("University of Tartu"))
			.andExpect(jsonPath("$.degree").value("BSc"))
			.andExpect(jsonPath("$.field").value("Computer Science"))
			.andExpect(jsonPath("$.startDate").value("2018-09-01"))
			.andExpect(jsonPath("$.endDate").value("2021-06-30"))
			.andExpect(jsonPath("$.bullets.length()").value(1))
			.andExpect(jsonPath("$.bullets[0].id").isNumber())
			.andExpect(jsonPath("$.bullets[0].content").value("Studied algorithms and databases"))
			.andReturn();

		long id = readId(createResult);

		Assertions.assertTrue(
				createResult.getResponse().getHeader("Location").endsWith("/api/educations/" + id)
		);

		mockMvc.perform(get("/api/educations/" + id))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.institution").value("University of Tartu"))
			.andExpect(jsonPath("$.bullets[0].id").isNumber());

		mockMvc.perform(get("/api/educations"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(1));
	}

	@Test
	void updateEducation_replacesBullets() throws Exception {
		createProfile();
		long id = createEducation();

		mockMvc.perform(put("/api/educations/" + id)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "institution": "Tallinn University of Technology",
						  "degree": "MSc",
						  "field": "Software Engineering",
						  "startDate": "2021-09-01",
						  "endDate": null,
						  "bullets": [
						    { "content": "Machine learning" },
						    { "content": "Distributed systems" }
						  ]
						}
						"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.institution").value("Tallinn University of Technology"))
			.andExpect(jsonPath("$.degree").value("MSc"))
			.andExpect(jsonPath("$.endDate").value(nullValue()))
			.andExpect(jsonPath("$.bullets.length()").value(2))
			.andExpect(jsonPath("$.bullets[0].id").isNumber())
			.andExpect(jsonPath("$.bullets[0].content").value("Machine learning"));
	}

	@Test
	void deleteEducation_removesEducation() throws Exception {
		createProfile();
		long id = createEducation();

		mockMvc.perform(delete("/api/educations/" + id))
			.andExpect(status().isNoContent());

		mockMvc.perform(get("/api/educations/" + id))
			.andExpect(status().isNotFound());

		mockMvc.perform(get("/api/educations"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(0));
	}

	@Test
	void getEducation_whenMissing_returnsNotFound() throws Exception {
		mockMvc.perform(get("/api/educations/999999"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("Education not found: 999999"));
	}

	@Test
	void createEducation_withInvalidDates_returnsBadRequest() throws Exception {
		createProfile();

		mockMvc.perform(post("/api/educations")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "institution": "University of Tartu",
						  "startDate": "2023-01-01",
						  "endDate": "2020-01-01",
						  "bullets": []
						}
						"""))
			.andExpect(status().isBadRequest());
	}

	@Test
	void createEducation_withTags_returnsTagsOnBullets() throws Exception {
		createProfile();
		long dataTagId = createTag("data", "Data");

		mockMvc.perform(post("/api/educations")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "institution": "University of Tartu",
						  "degree": "BSc",
						  "field": "Computer Science",
						  "startDate": "2018-09-01",
						  "endDate": "2021-06-30",
						  "bullets": [
						    {
						      "content": "Studied algorithms and databases",
						      "tagIds": [%d]
						    }
						  ]
						}
						""".formatted(dataTagId)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.bullets[0].tags.length()").value(1))
			.andExpect(jsonPath("$.bullets[0].tags[0].slug").value("data"));
	}

	@Test
	void createEducation_withMissingTag_returnsUnprocessableEntity() throws Exception {
		createProfile();

		mockMvc.perform(post("/api/educations")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "institution": "University of Tartu",
						  "degree": "BSc",
						  "field": "Computer Science",
						  "bullets": [
						    { "content": "Studied algorithms", "tagIds": [999999] }
						  ]
						}
						"""))
			.andExpect(status().isUnprocessableEntity())
			.andExpect(jsonPath("$.message").value("Tag not found: 999999"));
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
			.andExpect(status().is2xxSuccessful());
	}

	private long createEducation() throws Exception {
		MvcResult result = mockMvc.perform(post("/api/educations")
				.contentType(MediaType.APPLICATION_JSON)
				.content(sampleEducationJson()))
			.andExpect(status().isCreated())
			.andReturn();
		return readId(result);
	}

	private String sampleEducationJson() {
		return """
				{
				  "institution": "University of Tartu",
				  "degree": "BSc",
				  "field": "Computer Science",
				  "startDate": "2018-09-01",
				  "endDate": "2021-06-30",
				  "bullets": [
				    { "content": "Studied algorithms and databases" }
				  ]
				}
				""";
	}

	private long createTag(String slug, String name) throws Exception {
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
		return readId(result);
	}

	private long readId(MvcResult result) throws Exception {
		JsonNode json = jsonMapper.readTree(result.getResponse().getContentAsString());
		return json.get("id").asLong();
	}

}
