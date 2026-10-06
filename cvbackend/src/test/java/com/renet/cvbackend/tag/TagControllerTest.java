package com.renet.cvbackend.tag;

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
class TagControllerTest {

	private final JsonMapper jsonMapper = JsonMapper.builder().build();

	@Autowired
	private MockMvc mockMvc;

	@Test
	void listTags_whenEmpty_returnsEmptyList() throws Exception {
		mockMvc.perform(get("/api/tags"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$.length()").value(0));
	}

	@Test
	void createAndGetTag_returnsTag() throws Exception {
		MvcResult createResult = mockMvc.perform(post("/api/tags")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "slug": "java",
						  "name": "Java"
						}
						"""))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").isNumber())
			.andExpect(jsonPath("$.slug").value("java"))
			.andExpect(jsonPath("$.name").value("Java"))
			.andReturn();

		long id = readId(createResult);

		Assertions.assertTrue(
				createResult.getResponse().getHeader("Location").endsWith("/api/tags/" + id)
		);

		mockMvc.perform(get("/api/tags/" + id))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.slug").value("java"));

		mockMvc.perform(get("/api/tags"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(1));
	}

	@Test
	void createTag_withDuplicateSlug_returnsConflict() throws Exception {
		createTag("java", "Java");

		mockMvc.perform(post("/api/tags")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "slug": "java",
						  "name": "Java Language"
						}
						"""))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.message").value("Tag slug already exists: java"));
	}

	@Test
	void updateTag_changesSlugAndName() throws Exception {
		long id = createTag("java", "Java");

		mockMvc.perform(put("/api/tags/" + id)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "slug": "spring",
						  "name": "Spring"
						}
						"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.slug").value("spring"))
			.andExpect(jsonPath("$.name").value("Spring"));
	}

	@Test
	void deleteTag_removesTag() throws Exception {
		long id = createTag("java", "Java");

		mockMvc.perform(delete("/api/tags/" + id))
			.andExpect(status().isNoContent());

		mockMvc.perform(get("/api/tags/" + id))
			.andExpect(status().isNotFound());
	}

	@Test
	void getTag_whenMissing_returnsNotFound() throws Exception {
		mockMvc.perform(get("/api/tags/999999"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("Tag not found: 999999"));
	}

	@Test
	void createTag_withInvalidSlug_returnsBadRequest() throws Exception {
		mockMvc.perform(post("/api/tags")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "slug": "Java_Lang",
						  "name": "Java"
						}
						"""))
			.andExpect(status().isBadRequest());
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

	private long readId(MvcResult result) throws Exception {
		JsonNode json = jsonMapper.readTree(result.getResponse().getContentAsString());
		return json.get("id").asLong();
	}

}
