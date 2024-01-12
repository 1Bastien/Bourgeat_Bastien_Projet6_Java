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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;

import com.paymybuddy.app.DTO.PasswordDTO;
import com.paymybuddy.app.DTO.UserDTO;
import com.paymybuddy.app.service.UserAccountService;

@WebMvcTest(UserAccountController.class)
public class UserAccountControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserAccountService accountService;

	@Test
	@WithMockUser(username = "john@test.com", roles = "USER")
	void testGetAccount() throws Exception {
		UserDTO user = new UserDTO();
		PasswordDTO passwordDTO = new PasswordDTO();

		when(accountService.getAccount(any(Model.class))).thenReturn("userAccount");

		mockMvc.perform(get("/userAccount").flashAttr("user", user).flashAttr("passwordDTO", passwordDTO))
				.andExpect(status().isOk()).andExpect(view().name("userAccount"))
				.andExpect(model().attributeExists("user")).andExpect(model().attributeExists("passwordDTO"));

		verify(accountService, times(1)).getAccount(any(Model.class));
	}

	@Test
	@WithMockUser(username = "john@test.com", roles = "USER")
	void testPostAccount() throws Exception {
		UserDTO user = new UserDTO();
		user.setFirstName("John");
		user.setLastName("Doe");
		user.setBillingAddress("1 rue du test");

		when(accountService.postAccount(any(UserDTO.class), any())).thenReturn("redirect:/userAccount");

		mockMvc.perform(post("/userAccount").with(csrf()).flashAttr("user", user))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/userAccount"));

		verify(accountService, times(1)).postAccount(any(UserDTO.class), any());
	}

	@Test
	@WithMockUser(username = "john@test.com", roles = "USER")
	void testPostAccountWithErrors() throws Exception {
		UserDTO user = new UserDTO();

		when(accountService.postAccount(any(UserDTO.class), any())).thenReturn("redirect:/userAccount");

		mockMvc.perform(post("/userAccount").with(csrf()).flashAttr("user", user))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/userAccount"));

		verify(accountService, never()).postAccount(any(UserDTO.class), any());
	}
	
	@Test
	@WithMockUser(username = "john@test.com", roles = "USER")
	void testPostPassword() throws Exception {
		PasswordDTO passwordDTO = new PasswordDTO();
		passwordDTO.setNewPassword("testtest");
		passwordDTO.setOldPassword("testtest");

		when(accountService.changePassword(any(PasswordDTO.class), any())).thenReturn("redirect:/userAccount");

		mockMvc.perform(post("/userAccount/password").with(csrf()).flashAttr("passwordDTO", passwordDTO))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/userAccount"));

		verify(accountService, times(1)).changePassword(any(PasswordDTO.class), any());
	}
	
	@Test
	@WithMockUser(username = "john@test.com", roles = "USER")
	void testPostPasswordWithErrors() throws Exception {
		PasswordDTO passwordDTO = new PasswordDTO();

		when(accountService.changePassword(any(PasswordDTO.class), any())).thenReturn("redirect:/userAccount");

		mockMvc.perform(post("/userAccount/password").with(csrf()).flashAttr("passwordDTO", passwordDTO))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/userAccount"));

		verify(accountService, never()).changePassword(any(PasswordDTO.class), any());
	}
}
