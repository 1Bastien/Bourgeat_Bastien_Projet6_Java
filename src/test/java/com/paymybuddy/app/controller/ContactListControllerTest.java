package com.paymybuddy.app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.paymybuddy.app.DTO.ContactDTO;
import com.paymybuddy.app.service.ContactListService;

@WebMvcTest(ContactListController.class)
public class ContactListControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ContactListService contactListService;

	@Test
	@WithMockUser(username = "john@test.com", roles = "USER")
	void testGetContactList() throws Exception {
		ContactDTO contactDTO = new ContactDTO();
		List<String> contactList = new ArrayList<>();

		when(contactListService.getContactList(any(Model.class))).thenReturn("contactList");

		mockMvc.perform(get("/contactList").flashAttr("contactDTO", contactDTO).flashAttr("contactList", contactList))
				.andExpect(status().isOk()).andExpect(view().name("contactList"))
				.andExpect(model().attributeExists("contactDTO")).andExpect(model().attributeExists("contactList"));

		verify(contactListService, times(1)).getContactList(any(Model.class));
	}

	@Test
	@WithMockUser(username = "john@test.com", roles = "USER")
	void testPostAccountContactList() throws Exception {
		ContactDTO contactDTO = new ContactDTO();
		contactDTO.setContact("test@test.com");

		when(contactListService.addOrDelContact(any(ContactDTO.class), eq(true), any(RedirectAttributes.class)))
				.thenReturn("contactList");

		mockMvc.perform(post("/contactList").with(csrf()).param("addOrDel", "true").flashAttr("contactDTO", contactDTO))
				.andExpect(status().isOk()).andExpect(view().name("contactList"))
				.andExpect(model().attributeExists("contactDTO"));

		verify(contactListService, times(1)).addOrDelContact(eq(contactDTO), eq(true), any(RedirectAttributes.class));
	}

	@Test
	@WithMockUser(username = "john@test.com", roles = "USER")
	void testPostAccountContactListWithValidationErrors() throws Exception {
		ContactDTO contactDTO = new ContactDTO();
		contactDTO.setContact(null);

		when(contactListService.addOrDelContact(any(ContactDTO.class), eq(true), any(RedirectAttributes.class)))
				.thenReturn("contactList");

		mockMvc.perform(post("/contactList").with(csrf()).param("addOrDel", "true").flashAttr("contactDTO", contactDTO))
				.andExpect(status().isOk()).andExpect(view().name("contactList"))
				.andExpect(model().attributeHasErrors("contactDTO"))
				.andExpect(model().attributeHasFieldErrors("contactDTO", "contact"));

		verify(contactListService, never()).addOrDelContact(any(ContactDTO.class), eq(true),
				any(RedirectAttributes.class));
	}
}
