package com.renet.cvbackend.profile;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProfileControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ProfileRepository profileRepository;

	@Test
	void getProfile_whenEmpty_returnsNotFound() throws Exception {
		mockMvc.perform(get("/api/profile"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("Profile not found"));
	}

	@Test
	void putProfile_whenEmpty_createsProfile() throws Exception {
		mockMvc.perform(put("/api/profile")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "fullName": "Jane Doe",
						  "headline": "Backend Developer",
						  "email": "jane@example.com",
						  "phone": "+372 5555 5555",
						  "location": "Tallinn, Estonia",
						  "contactLinks": [
						    { "label": "GitHub", "url": "https://github.com/janedoe" },
						    { "label": "LinkedIn", "url": "https://linkedin.com/in/janedoe" }
						  ]
						}
						"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").isNumber())
			.andExpect(jsonPath("$.fullName").value("Jane Doe"))
			.andExpect(jsonPath("$.headline").value("Backend Developer"))
			.andExpect(jsonPath("$.email").value("jane@example.com"))
			.andExpect(jsonPath("$.phone").value("+372 5555 5555"))
			.andExpect(jsonPath("$.location").value("Tallinn, Estonia"))
			.andExpect(jsonPath("$.contactLinks.length()").value(2))
			.andExpect(jsonPath("$.contactLinks[0].id").isNumber())
			.andExpect(jsonPath("$.contactLinks[0].label").value("GitHub"))
			.andExpect(jsonPath("$.contactLinks[0].url").value("https://github.com/janedoe"));
	}

	@Test
	void getProfile_afterCreate_returnsProfile() throws Exception {
		createSampleProfile();

		mockMvc.perform(get("/api/profile"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.fullName").value("Jane Doe"))
			.andExpect(jsonPath("$.contactLinks[0].id").isNumber());
	}

	@Test
	void putProfile_whenProfileExists_updatesProfileAndReplacesContactLinks() throws Exception {
		createSampleProfile();

		mockMvc.perform(put("/api/profile")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "fullName": "Jane Smith",
						  "headline": "Java Developer",
						  "email": "jane.smith@example.com",
						  "contactLinks": [
						    { "label": "Website", "url": "https://janesmith.example" }
						  ]
						}
						"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.fullName").value("Jane Smith"))
			.andExpect(jsonPath("$.headline").value("Java Developer"))
			.andExpect(jsonPath("$.email").value("jane.smith@example.com"))
			.andExpect(jsonPath("$.contactLinks.length()").value(1))
			.andExpect(jsonPath("$.contactLinks[0].label").value("Website"));

		assert profileRepository.count() == 1;
	}

	@Test
	void putProfile_withInvalidRequest_returnsBadRequest() throws Exception {
		mockMvc.perform(put("/api/profile")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "fullName": "",
						  "contactLinks": [
						    { "label": "GitHub", "url": "" }
						  ]
						}
						"""))
			.andExpect(status().isBadRequest());
	}

	private void createSampleProfile() throws Exception {
		mockMvc.perform(put("/api/profile")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "fullName": "Jane Doe",
						  "headline": "Backend Developer",
						  "email": "jane@example.com",
						  "contactLinks": [
						    { "label": "GitHub", "url": "https://github.com/janedoe" }
						  ]
						}
						"""));
	}

}
