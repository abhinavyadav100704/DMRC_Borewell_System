package com.dmrc.borewell;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureJson
@AutoConfigureMockMvc
class BorewellSystemApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	// ---------------- AUTH TESTS ----------------

	@Test
	void signupUser() throws Exception {

		String request = """
        {
            "username": "testuser",
            "email": "test@test.com",
            "password": "password123",
            "role": "user"
        }
        """;

		mockMvc.perform(post("/auth/signup")
						.contentType(MediaType.APPLICATION_JSON)
						.content(request))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message")
						.value("User registered successfully"));
	}

	@Test
	void loginUser() throws Exception {

		String request = """
        {
            "username": "testuser",
            "password": "password123"
        }
        """;

		mockMvc.perform(post("/auth/signin")
						.contentType(MediaType.APPLICATION_JSON)
						.content(request))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").exists())
				.andExpect(jsonPath("$.username").value("testuser"));
	}

	// ---------------- STATION TESTS ----------------

	@Test
	@WithMockUser(roles = "USER")
	void getAllStations() throws Exception {

		mockMvc.perform(get("/stations"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
	}

	// ---------------- AUTHORITY TESTS ----------------

	@Test
	@WithMockUser(roles = "USER")
	void getAllAuthorities() throws Exception {

		mockMvc.perform(get("/authorities"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
	}

	// ---------------- BOREWELL TESTS ----------------

	@Test
	@WithMockUser(roles = "USER")
	void getAllBorewells() throws Exception {

		mockMvc.perform(get("/borewells"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
	}






//	tests for update station and delete borewell to verify role-based access control
	@Test
	@WithMockUser(roles = "ADMIN")
	void updateStation_asAdmin_shouldSucceed() throws Exception {

		String request = """
    {
        "stationName": "Updated Station"
    }
    """;

		mockMvc.perform(put("/stations/2")
						.contentType(MediaType.APPLICATION_JSON)
						.content(request))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
	}

	@Test
	@WithMockUser(roles = "USER")
	void updateStation_asUser_shouldFail() throws Exception {

		String request = """
    {
        "stationName": "Updated Station"
    }
    """;

		mockMvc.perform(put("/stations/2")
						.contentType(MediaType.APPLICATION_JSON)
						.content(request))
				.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void deleteBorewell_asAdmin_shouldSucceed() throws Exception {

		mockMvc.perform(delete("/borewells/6"))
				.andExpect(status().isNoContent());
	}

	@Test
	@WithMockUser(roles = "USER")
	void deleteBorewell_asUser_shouldFail() throws Exception {

		mockMvc.perform(delete("/borewells/6"))
				.andExpect(status().isForbidden());
	}

}