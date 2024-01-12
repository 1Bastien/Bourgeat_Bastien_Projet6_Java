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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;

import com.paymybuddy.app.DTO.TransferDTO;
import com.paymybuddy.app.model.Transaction;
import com.paymybuddy.app.service.TransactionService;

@WebMvcTest(TransferController.class)
public class TransferControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TransactionService transactionService;

	@Test
	@WithMockUser(username = "john@test.com", roles = "USER")
	void testTransfer() throws Exception {
		TransferDTO transferDTO = new TransferDTO();
		BigDecimal balance = new BigDecimal(100);
		List<Transaction> transactionList = new ArrayList<Transaction>();
		List<String> contactList = new ArrayList<String>();

		when(transactionService.getTransaction(any(Model.class))).thenReturn("transfer");

		mockMvc.perform(get("/transfer").flashAttr("transferDTO", transferDTO).flashAttr("balance", balance)
				.flashAttr("transactionList", transactionList).flashAttr("contactList", contactList))
				.andExpect(status().isOk())
				.andExpect(view().name("transfer"))
				.andExpect(model().attributeExists("transferDTO"))
				.andExpect(model().attributeExists("balance"))
				.andExpect(model().attributeExists("transactionList"))
				.andExpect(model().attributeExists("contactList"));
		
		verify(transactionService, times(1)).getTransaction(any(Model.class));
	}
	
	@Test
	@WithMockUser(username = "john@test.com", roles = "USER")
	void testTransferPost() throws Exception {
		TransferDTO transferDTO = new TransferDTO();
		transferDTO.setContact("test@test.com");
		transferDTO.setAmount(new BigDecimal(50));
		
		when(transactionService.transferToContact(any(TransferDTO.class), any())).thenReturn("redirect:/transfer");

		mockMvc.perform(post("/transfer").with(csrf()).flashAttr("transferDTO", transferDTO))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/transfer"));

		verify(transactionService, times(1)).transferToContact(any(TransferDTO.class), any());
	}
	
	@Test
	@WithMockUser(username = "john@test.com", roles = "USER")
	void testTransferPostWithErrors() throws Exception {
		TransferDTO transferDTO = new TransferDTO();

		when(transactionService.transferToContact(any(TransferDTO.class), any())).thenReturn("redirect:/transfer");

		mockMvc.perform(post("/transfer").with(csrf()).flashAttr("transferDTO", transferDTO))
				.andExpect(status().isOk()).andExpect(view().name("transfer"));

		verify(transactionService, never()).transferToContact(any(TransferDTO.class), any());
	}
}
