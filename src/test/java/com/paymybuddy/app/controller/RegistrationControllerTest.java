package com.paymybuddy.app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.paymybuddy.app.model.MyUser;
import com.paymybuddy.app.service.RegistrationService;

@SpringBootTest
@AutoConfigureMockMvc
public class RegistrationControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private RegistrationService registrationService;

	@Test
	void testShowRegistrationForm() throws Exception {
		MyUser user = new MyUser();

		when(registrationService.showRegistrationForm(any(Model.class))).thenReturn("registration");

		mockMvc.perform(get("/registration").flashAttr("user", user))
		.andExpect(status().isOk())
		.andExpect(view().name("registration"))
		.andExpect(model().attributeExists("user"));

		verify(registrationService, times(1)).showRegistrationForm(any(Model.class));
	}
	
	@Test
	void testPostProcessRegistration() throws Exception {
		MyUser user = new MyUser();
		user.setEmail("test@test.com");
		user.setPassword("testtest");
		user.setFirstName("test");
		user.setLastName("test");
		user.setBillingAddress("test");

		when(registrationService.processRegistration(any(MyUser.class), any(RedirectAttributes.class))).thenReturn("redirect:/login");
		
		mockMvc.perform(post("/registration").with(csrf()).flashAttr("user", user))
		.andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/login"));
		
		verify(registrationService, times(1)).processRegistration(any(MyUser.class), any(RedirectAttributes.class));
	}
	
	@Test
	void testPostProcessRegistrationWithErrors() throws Exception {
		MyUser user = new MyUser();

		when(registrationService.processRegistration(any(MyUser.class), any(RedirectAttributes.class))).thenReturn("redirect:/login");
		
		mockMvc.perform(post("/registration").with(csrf()).flashAttr("user", user))
		.andExpect(status().isOk())
		.andExpect(view().name("registration"))
		.andExpect(model().attributeExists("user"));
		
		verify(registrationService, never()).processRegistration(any(MyUser.class), any(RedirectAttributes.class));
	}
}
