package com.dmrc.borewell;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BorewellSystemApplicationTests {

	@TestConfiguration
	static class TestConfig {
		@Bean
		public ObjectMapper objectMapper() {
			return new ObjectMapper();
		}
	}

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	// ---------------- AUTH TESTS ----------------

	@Test
	void signupUser() throws Exception {
		String request = """
        {
            "username": "newuser",
            "email": "signup123@test.com",
            "password": "password12345",
            "role": "user"
        }
        """;
		mockMvc.perform(post("/auth/signup")
						.contentType(MediaType.APPLICATION_JSON)
						.content(request))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("User registered successfully"));
	}

	@Test
	void loginUser() throws Exception {
		// First signup the user
		String signup = """
        {
            "username": "testuser_login",
            "email": "login@test.com",
            "password": "password123",
            "role": "user"
        }
        """;
		mockMvc.perform(post("/auth/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(signup));

		// Then login
		String request = """
        {
            "username": "testuser_login",
            "password": "password123"
        }
        """;
		mockMvc.perform(post("/auth/signin")
						.contentType(MediaType.APPLICATION_JSON)
						.content(request))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").exists())
				.andExpect(jsonPath("$.username").value("testuser_login"));
	}

	// ---------------- STATION TESTS ----------------

	@Test
	void getAllStations() throws Exception {
		mockMvc.perform(get("/stations")
						.with(user("testuser").roles("USER")))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
	}

	// ---------------- AUTHORITY TESTS ----------------

	@Test
	void getAllAuthorities() throws Exception {
		mockMvc.perform(get("/authorities")
						.with(user("testuser").roles("USER")))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
	}

	// ---------------- BOREWELL TESTS ----------------

	@Test
	void getAllBorewells() throws Exception {
		mockMvc.perform(get("/borewells")
						.with(user("testuser").roles("USER")))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
	}

	@Test
	void updateStation_asAdmin_shouldSucceed() throws Exception {
		String request = """
    {
        "stationName": "Updated Station",
        "lineId": 1,
        "location": "Test Location",
        "stationType": "ELEVATED",
        "platformCount": 2,
        "openingDate": "2020-01-01",
        "lastMaintenanceDate": "2024-01-01",
        "maintenanceNotes": "Test notes"
    }
    """;
		mockMvc.perform(put("/stations/2")
						.with(user("admin").roles("ADMIN"))
						.contentType(MediaType.APPLICATION_JSON)
						.content(request))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
	}

	@Test
	void updateStation_asUser_shouldFail() throws Exception {
		String request = """
    {
        "stationName": "Updated Station",
        "lineId": 1,
        "location": "Test Location",
        "stationType": "ELEVATED",
        "platformCount": 2,
        "openingDate": "2020-01-01",
        "lastMaintenanceDate": "2024-01-01",
        "maintenanceNotes": "Test notes"
    }
    """;
		mockMvc.perform(put("/stations/2")
						.with(user("testuser").roles("USER"))
						.contentType(MediaType.APPLICATION_JSON)
						.content(request))
				.andExpect(status().isForbidden());
	}

	@Test
	void deleteBorewell_asAdmin_shouldSucceed() throws Exception {
		mockMvc.perform(delete("/borewells/8")
						.with(user("admin").roles("ADMIN")))
				.andExpect(status().isNoContent());
	}

	@Test
	void deleteBorewell_asUser_shouldFail() throws Exception {
		mockMvc.perform(delete("/borewells/8")
						.with(user("testuser").roles("USER")))
				.andExpect(status().isForbidden());
	}
}