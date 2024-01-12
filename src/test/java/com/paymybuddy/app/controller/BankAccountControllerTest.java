package com.paymybuddy.app.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.paymybuddy.app.DTO.AmountDTO;
import com.paymybuddy.app.service.BankAccountService;

@WebMvcTest(BankAccountController.class)
public class BankAccountControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BankAccountService bankAccountService;

	@Test
	@WithMockUser(username = "john@test.com", roles = "USER")
	void testGetReloadAccount() throws Exception {
		AmountDTO amountDTO = new AmountDTO();

		mockMvc.perform(get("/reloadAccount").flashAttr("amountDTO", amountDTO)).andExpect(status().isOk())
				.andExpect(view().name("reloadAccount"));
	}

	@Test
	@WithMockUser(username = "john@test.com", roles = "USER")
	void testGetTransferToBank() throws Exception {
		AmountDTO amountDTO = new AmountDTO();

		mockMvc.perform(get("/transferToBank").flashAttr("amountDTO", amountDTO)).andExpect(status().isOk())
				.andExpect(view().name("transferToBank"));
	}

	@Test
	@WithMockUser(username = "john@test.com", roles = "USER")
	void testReloadAccount() throws Exception {
		AmountDTO amountDTO = new AmountDTO();

		mockMvc.perform(post("/reloadAccount").with(csrf()).flashAttr("amountDTO", amountDTO))
				.andExpect(status().isOk());
	}
}
