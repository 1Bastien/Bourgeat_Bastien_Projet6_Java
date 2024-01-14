package com.paymybuddy.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import com.paymybuddy.app.DTO.ContactDTO;
import com.paymybuddy.app.model.MyUser;
import com.paymybuddy.app.repository.MyUserRepository;

@ExtendWith(MockitoExtension.class)
public class ContactListServiceTest {

	@InjectMocks
	private ContactListService contactListService;

	@Mock
	private MyUserRepository myUserRepository;

	@Mock
	private Model model;

	@Mock
	private Authentication authentication;

	@Test
	void testGetContactList() throws Exception {
		MyUser myUser = new MyUser();
		myUser.setEmail("test@test.com");

		List<String> contactList = new ArrayList<String>();
		contactList.add("test2@test.com");

		SecurityContextHolder.getContext().setAuthentication(authentication);
		when(authentication.getName()).thenReturn("test@example.com");

		when(myUserRepository.findByEmail(anyString())).thenReturn(java.util.Optional.of(myUser));

		String result = contactListService.getContactList(model);

		verify(myUserRepository, times(1)).findByEmail(anyString());
		verify(model, times(1)).addAttribute(eq("contactList"), any());
		verify(model, times(1)).addAttribute(eq("contactDTO"), any(ContactDTO.class));

		assertEquals("contactList", result);
	}

	@Test
	void testAddOrDelContact_AddContact_Success() throws Exception {
		ContactDTO contactDTO = new ContactDTO();
		contactDTO.setContact("ajoutContact@test.com");
		Boolean addOrDel = true;
		RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

		MyUser loggedInUser = new MyUser();
		loggedInUser.setEmail("user@test.com");
		loggedInUser.setContactList(new ArrayList<>());

		SecurityContextHolder.getContext().setAuthentication(authentication);
		when(authentication.getName()).thenReturn("user@test.com");

		MyUser existingUser = new MyUser();
		existingUser.setEmail("ajoutContact@test.com");

		when(myUserRepository.findByEmail("ajoutContact@test.com")).thenReturn(Optional.of(existingUser));
		when(myUserRepository.findByEmail("user@test.com")).thenReturn(Optional.of(loggedInUser));

		String result = contactListService.addOrDelContact(contactDTO, addOrDel, redirectAttributes);

		verify(myUserRepository, times(2)).findByEmail(anyString());
		verify(myUserRepository, times(1)).save(eq(loggedInUser));

		assertEquals("redirect:/contactList", result);
		assertEquals("Contact ajouté", redirectAttributes.getFlashAttributes().get("success"));
	}

	@Test
	void testAddOrDelContact_AddContact_ContactNotFound() throws Exception {
		ContactDTO contactDTO = new ContactDTO();
		contactDTO.setContact("ajoutContact@test.com");
		Boolean addOrDel = true;
		RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

		MyUser existingUser = new MyUser();
		existingUser.setEmail("ajoutContact@test.com");

		when(myUserRepository.findByEmail("ajoutContact@test.com")).thenReturn(Optional.empty());

		String result = contactListService.addOrDelContact(contactDTO, addOrDel, redirectAttributes);

		verify(myUserRepository, times(1)).findByEmail(anyString());
		verify(myUserRepository, never()).save(any());

		assertEquals("redirect:/contactList", result);
		assertEquals("Contact introuvable", redirectAttributes.getFlashAttributes().get("error"));
	}

	@Test
	void testAddOrDelContact_AddContact_addYourself() throws Exception {
		ContactDTO contactDTO = new ContactDTO();
		contactDTO.setContact("user@test.com");
		Boolean addOrDel = true;
		RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

		MyUser loggedInUser = new MyUser();
		loggedInUser.setEmail("user@test.com");
		loggedInUser.setContactList(new ArrayList<>());

		SecurityContextHolder.getContext().setAuthentication(authentication);
		when(authentication.getName()).thenReturn("user@test.com");

		MyUser existingUser = new MyUser();
		existingUser.setEmail("user@test.com");

		when(myUserRepository.findByEmail("user@test.com")).thenReturn(Optional.of(existingUser));

		String result = contactListService.addOrDelContact(contactDTO, addOrDel, redirectAttributes);

		verify(myUserRepository, times(2)).findByEmail(anyString());
		verify(myUserRepository, never()).save(eq(loggedInUser));

		assertEquals("redirect:/contactList", result);
		assertEquals("Vous ne pouvez pas vous ajouter vous-même", redirectAttributes.getFlashAttributes().get("error"));
	}

	@Test
	void testAddOrDelContact_AddContact_ContactAlreadyAdd() throws Exception {
		ContactDTO contactDTO = new ContactDTO();
		contactDTO.setContact("ajoutContact@test.com");
		Boolean addOrDel = true;
		RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

		MyUser loggedInUser = new MyUser();
		loggedInUser.setEmail("user@test.com");
		loggedInUser.setContactList(new ArrayList<>());
		loggedInUser.getContactList().add("ajoutContact@test.com");

		SecurityContextHolder.getContext().setAuthentication(authentication);
		when(authentication.getName()).thenReturn("user@test.com");

		MyUser existingUser = new MyUser();
		existingUser.setEmail("ajoutContact@test.com");

		when(myUserRepository.findByEmail("ajoutContact@test.com")).thenReturn(Optional.of(existingUser));
		when(myUserRepository.findByEmail("user@test.com")).thenReturn(Optional.of(loggedInUser));

		String result = contactListService.addOrDelContact(contactDTO, addOrDel, redirectAttributes);

		verify(myUserRepository, times(2)).findByEmail(anyString());
		verify(myUserRepository, never()).save(any(MyUser.class));

		assertEquals("redirect:/contactList", result);
		assertEquals("Le contact est déjà dans la liste", redirectAttributes.getFlashAttributes().get("error"));
	}
	
	@Test
	void testAddOrDelContact_DelContact_Success() throws Exception {
		ContactDTO contactDTO = new ContactDTO();
		contactDTO.setContact("ajoutContact@test.com");
		Boolean addOrDel = false;
		RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

		MyUser loggedInUser = new MyUser();
		loggedInUser.setEmail("user@test.com");
		loggedInUser.setContactList(new ArrayList<>());
		loggedInUser.getContactList().add("ajoutContact@test.com");

		SecurityContextHolder.getContext().setAuthentication(authentication);
		when(authentication.getName()).thenReturn("user@test.com");

		MyUser existingUser = new MyUser();
		existingUser.setEmail("ajoutContact@test.com");

		when(myUserRepository.findByEmail("ajoutContact@test.com")).thenReturn(Optional.of(existingUser));
		when(myUserRepository.findByEmail("user@test.com")).thenReturn(Optional.of(loggedInUser));

		String result = contactListService.addOrDelContact(contactDTO, addOrDel, redirectAttributes);

		verify(myUserRepository, times(2)).findByEmail(anyString());
		verify(myUserRepository, times(1)).save(eq(loggedInUser));

		assertEquals("redirect:/contactList", result);
		assertEquals("Contact supprimé", redirectAttributes.getFlashAttributes().get("success"));
	}
	
	@Test
	void testAddOrDelContact_DelContact_NotFound() throws Exception {
		ContactDTO contactDTO = new ContactDTO();
		contactDTO.setContact("ajoutContact@test.com");
		Boolean addOrDel = false;
		RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

		MyUser loggedInUser = new MyUser();
		loggedInUser.setEmail("user@test.com");
		loggedInUser.setContactList(new ArrayList<>());

		SecurityContextHolder.getContext().setAuthentication(authentication);
		when(authentication.getName()).thenReturn("user@test.com");

		MyUser existingUser = new MyUser();
		existingUser.setEmail("ajoutContact@test.com");

		when(myUserRepository.findByEmail("ajoutContact@test.com")).thenReturn(Optional.of(existingUser));
		when(myUserRepository.findByEmail("user@test.com")).thenReturn(Optional.of(loggedInUser));

		String result = contactListService.addOrDelContact(contactDTO, addOrDel, redirectAttributes);

		verify(myUserRepository, times(2)).findByEmail(anyString());
		verify(myUserRepository, never()).save(eq(loggedInUser));

		assertEquals("redirect:/contactList", result);
		assertEquals("Le contact n'est pas dans la liste", redirectAttributes.getFlashAttributes().get("error"));
	}
}
