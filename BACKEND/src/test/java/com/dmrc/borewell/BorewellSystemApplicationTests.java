package com.dmrc.borewell;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
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
				.andExpect(status().isOk());
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
				.andExpect(jsonPath("$.token").exists());
	}

	// ---------------- STATION TESTS ----------------

	@Test
	void getAllStations() throws Exception {

		mockMvc.perform(get("/stations"))
				.andExpect(status().isOk());
	}

	// ---------------- AUTHORITY TESTS ----------------

	@Test
	void getAllAuthorities() throws Exception {

		mockMvc.perform(get("/authorities"))
				.andExpect(status().isOk());
	}

	// ---------------- BOREWELL TESTS ----------------

	@Test
	void getAllBorewells() throws Exception {

		mockMvc.perform(get("/borewells"))
				.andExpect(status().isOk());
	}

}