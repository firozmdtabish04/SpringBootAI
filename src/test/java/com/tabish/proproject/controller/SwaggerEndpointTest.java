
package com.tabish.proproject.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ActiveProfiles("h2")
class SwaggerEndpointTest {

	@Autowired
	private WebApplicationContext context;

	@Test
	@DisplayName("Test /v3/api-docs endpoint")
	void testOpenApiDocsEndpoint() throws Exception {

		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();

		MvcResult result = mockMvc.perform(get("/v3/api-docs")).andReturn();

		System.out.println("API-DOCS Status: " + result.getResponse().getStatus());

		System.out.println("API-DOCS Content: " + result.getResponse().getContentAsString());

		if (result.getResolvedException() != null) {
			result.getResolvedException().printStackTrace();
		}
	}
}
