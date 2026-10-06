package com.renet.cvbackend.roleprofile;

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
class RoleProfileControllerTest {

	private final JsonMapper jsonMapper = JsonMapper.builder().build();

	@Autowired
	private MockMvc mockMvc;

	@Test
	void listRoleProfiles_whenEmpty_returnsEmptyList() throws Exception {
		mockMvc.perform(get("/api/role-profiles"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$.length()").value(0));
	}

	@Test
	void createAndGetRoleProfile_returnsWeightedTags() throws Exception {
		long javaTagId = createTag("java", "Java");
		long springTagId = createTag("spring", "Spring");

		MvcResult createResult = mockMvc.perform(post("/api/role-profiles")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "slug": "java",
						  "name": "Java Developer",
						  "type": "PERMANENT",
						  "sortOrder": 1,
						  "tags": [
						    { "tagId": %d, "weight": 1.000 },
						    { "tagId": %d, "weight": 0.900 }
						  ]
						}
						""".formatted(javaTagId, springTagId)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").isNumber())
			.andExpect(jsonPath("$.slug").value("java"))
			.andExpect(jsonPath("$.name").value("Java Developer"))
			.andExpect(jsonPath("$.type").value("PERMANENT"))
			.andExpect(jsonPath("$.sortOrder").value(1))
			.andExpect(jsonPath("$.tags.length()").value(2))
			.andExpect(jsonPath("$.tags[0].tagId").value(javaTagId))
			.andExpect(jsonPath("$.tags[0].slug").value("java"))
			.andExpect(jsonPath("$.tags[0].weight").value(1.0))
			.andReturn();

		long id = readId(createResult);

		Assertions.assertTrue(
				createResult.getResponse().getHeader("Location").endsWith("/api/role-profiles/" + id)
		);

		mockMvc.perform(get("/api/role-profiles/" + id))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.tags.length()").value(2));
	}

	@Test
	void createRoleProfile_withMissingTag_returnsUnprocessableEntity() throws Exception {
		mockMvc.perform(post("/api/role-profiles")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "slug": "java",
						  "name": "Java Developer",
						  "type": "PERMANENT",
						  "sortOrder": 1,
						  "tags": [
						    { "tagId": 999999, "weight": 1.000 }
						  ]
						}
						"""))
			.andExpect(status().isUnprocessableEntity())
			.andExpect(jsonPath("$.message").value("Tag not found: 999999"));
	}

	@Test
	void createRoleProfile_withSecondGeneral_returnsConflict() throws Exception {
		createRoleProfile("general", "General", "GENERAL", 0);

		mockMvc.perform(post("/api/role-profiles")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "slug": "general-2",
						  "name": "Another General",
						  "type": "GENERAL",
						  "sortOrder": 1,
						  "tags": []
						}
						"""))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.message").value("Only one GENERAL role profile is allowed"));
	}

	@Test
	void updateRoleProfile_replacesTags() throws Exception {
		long javaTagId = createTag("java", "Java");
		long backendTagId = createTag("backend", "Backend");
		long id = createRoleProfile("java", "Java Developer", "PERMANENT", 1);

		mockMvc.perform(put("/api/role-profiles/" + id)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "slug": "java",
						  "name": "Java Backend",
						  "type": "PERMANENT",
						  "sortOrder": 2,
						  "tags": [
						    { "tagId": %d, "weight": 1.000 },
						    { "tagId": %d, "weight": 0.800 }
						  ]
						}
						""".formatted(javaTagId, backendTagId)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value("Java Backend"))
			.andExpect(jsonPath("$.sortOrder").value(2))
			.andExpect(jsonPath("$.tags.length()").value(2));
	}

	@Test
	void deleteRoleProfile_removesRoleProfile() throws Exception {
		long id = createRoleProfile("java", "Java Developer", "PERMANENT", 1);

		mockMvc.perform(delete("/api/role-profiles/" + id))
			.andExpect(status().isNoContent());

		mockMvc.perform(get("/api/role-profiles/" + id))
			.andExpect(status().isNotFound());
	}

	@Test
	void getRoleProfile_whenMissing_returnsNotFound() throws Exception {
		mockMvc.perform(get("/api/role-profiles/999999"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("Role profile not found: 999999"));
	}

	private long createTag(String slug, String name) throws Exception {
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

	private long createRoleProfile(String slug, String name, String type, int sortOrder) throws Exception {
		MvcResult result = mockMvc.perform(post("/api/role-profiles")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "slug": "%s",
						  "name": "%s",
						  "type": "%s",
						  "sortOrder": %d,
						  "tags": []
						}
						""".formatted(slug, name, type, sortOrder)))
			.andExpect(status().isCreated())
			.andReturn();
		return readId(result);
	}

	private long readId(MvcResult result) throws Exception {
		JsonNode json = jsonMapper.readTree(result.getResponse().getContentAsString());
		return json.get("id").asLong();
	}

}
