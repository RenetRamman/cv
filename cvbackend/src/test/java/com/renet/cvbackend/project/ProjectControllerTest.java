package com.renet.cvbackend.project;

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
class ProjectControllerTest {

	private final JsonMapper jsonMapper = JsonMapper.builder().build();

	@Autowired
	private MockMvc mockMvc;

	@Test
	void listProjects_whenEmpty_returnsEmptyList() throws Exception {
		mockMvc.perform(get("/api/projects"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$.length()").value(0));
	}

	@Test
	void createProject_withoutProfile_returnsUnprocessableEntity() throws Exception {
		mockMvc.perform(post("/api/projects")
				.contentType(MediaType.APPLICATION_JSON)
				.content(sampleProjectJson()))
			.andExpect(status().isUnprocessableEntity())
			.andExpect(jsonPath("$.message").value("Profile not found"));
	}

	@Test
	void createAndGetProject_returnsProjectWithNestedIds() throws Exception {
		createProfile();

		MvcResult createResult = mockMvc.perform(post("/api/projects")
				.contentType(MediaType.APPLICATION_JSON)
				.content(sampleProjectJson()))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").isNumber())
			.andExpect(jsonPath("$.title").value("CV Portfolio"))
			.andExpect(jsonPath("$.description").value("Interactive CV backend"))
			.andExpect(jsonPath("$.githubUrl").value("https://github.com/example/cv"))
			.andExpect(jsonPath("$.liveUrl").value("https://cv.example.com"))
			.andExpect(jsonPath("$.bullets.length()").value(1))
			.andExpect(jsonPath("$.bullets[0].id").isNumber())
			.andExpect(jsonPath("$.bullets[0].content").value("Built REST APIs with Spring Boot"))
			.andExpect(jsonPath("$.technologies.length()").value(2))
			.andExpect(jsonPath("$.technologies[0].id").isNumber())
			.andExpect(jsonPath("$.technologies[0].name").value("Java"))
			.andExpect(jsonPath("$.technologies[1].name").value("PostgreSQL"))
			.andReturn();

		long id = readId(createResult);

		Assertions.assertTrue(
				createResult.getResponse().getHeader("Location").endsWith("/api/projects/" + id)
		);

		mockMvc.perform(get("/api/projects/" + id))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.title").value("CV Portfolio"))
			.andExpect(jsonPath("$.bullets[0].id").isNumber())
			.andExpect(jsonPath("$.technologies[0].id").isNumber());

		mockMvc.perform(get("/api/projects"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(1));
	}

	@Test
	void updateProject_replacesBulletsAndTechnologies() throws Exception {
		createProfile();
		long id = createProject();

		mockMvc.perform(put("/api/projects/" + id)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "title": "CV Portfolio v2",
						  "description": null,
						  "githubUrl": "https://github.com/example/cv",
						  "liveUrl": null,
						  "bullets": [
						    { "content": "Added role-aware scoring" },
						    { "content": "Improved admin UX" }
						  ],
						  "technologies": [
						    { "name": "React" }
						  ]
						}
						"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.title").value("CV Portfolio v2"))
			.andExpect(jsonPath("$.description").value(nullValue()))
			.andExpect(jsonPath("$.liveUrl").value(nullValue()))
			.andExpect(jsonPath("$.bullets.length()").value(2))
			.andExpect(jsonPath("$.bullets[0].id").isNumber())
			.andExpect(jsonPath("$.bullets[0].content").value("Added role-aware scoring"))
			.andExpect(jsonPath("$.technologies.length()").value(1))
			.andExpect(jsonPath("$.technologies[0].name").value("React"));
	}

	@Test
	void deleteProject_removesProject() throws Exception {
		createProfile();
		long id = createProject();

		mockMvc.perform(delete("/api/projects/" + id))
			.andExpect(status().isNoContent());

		mockMvc.perform(get("/api/projects/" + id))
			.andExpect(status().isNotFound());

		mockMvc.perform(get("/api/projects"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(0));
	}

	@Test
	void getProject_whenMissing_returnsNotFound() throws Exception {
		mockMvc.perform(get("/api/projects/999999"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("Project not found: 999999"));
	}

	@Test
	void createProject_withInvalidRequest_returnsBadRequest() throws Exception {
		createProfile();

		mockMvc.perform(post("/api/projects")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "title": "",
						  "bullets": [
						    { "content": "" }
						  ],
						  "technologies": [
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

	private long createProject() throws Exception {
		MvcResult result = mockMvc.perform(post("/api/projects")
				.contentType(MediaType.APPLICATION_JSON)
				.content(sampleProjectJson()))
			.andExpect(status().isCreated())
			.andReturn();
		return readId(result);
	}

	private String sampleProjectJson() {
		return """
				{
				  "title": "CV Portfolio",
				  "description": "Interactive CV backend",
				  "githubUrl": "https://github.com/example/cv",
				  "liveUrl": "https://cv.example.com",
				  "bullets": [
				    { "content": "Built REST APIs with Spring Boot" }
				  ],
				  "technologies": [
				    { "name": "Java" },
				    { "name": "PostgreSQL" }
				  ]
				}
				""";
	}

	private long readId(MvcResult result) throws Exception {
		JsonNode json = jsonMapper.readTree(result.getResponse().getContentAsString());
		return json.get("id").asLong();
	}

}
