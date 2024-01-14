package com.paymybuddy.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import com.paymybuddy.app.model.MyUser;
import com.paymybuddy.app.repository.MyUserRepository;

@ExtendWith(MockitoExtension.class)
public class RegistrationServiceTest {

	@InjectMocks
	private RegistrationService registrationService;

	@Mock
	private MyUserRepository myUserRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private Model model;

	@Test
	void testShowRegistrationForm() throws Exception {
		String result = registrationService.showRegistrationForm(model);

		verify(model, times(1)).addAttribute(eq("user"), any(MyUser.class));
		assertEquals("registration", result);
	}

	@Test
	void testProcessRegistration_SuccessfulRegistration() throws Exception {
		MyUser user = new MyUser();
		user.setEmail("test@example.com");
		user.setPassword("password123");

		RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
		
		when(myUserRepository.findByEmail(anyString())).thenReturn(Optional.empty());
		when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

		String result = registrationService.processRegistration(user, redirectAttributes);

		verify(myUserRepository, times(1)).findByEmail(eq("test@example.com"));
		verify(passwordEncoder, times(1)).encode(eq("password123"));
		verify(myUserRepository, times(1)).save(eq(user));

		assertEquals("redirect:/login", result);
	}

	@Test
	void testProcessRegistration_DuplicateEmail() throws Exception {
		MyUser user = new MyUser();
		user.setEmail("test@example.com");
		user.setPassword("password123");

		RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
		
		when(myUserRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

		String result = registrationService.processRegistration(user, redirectAttributes);

		verify(myUserRepository, times(1)).findByEmail(eq("test@example.com"));
		verify(passwordEncoder, never()).encode(anyString());
		verify(myUserRepository, never()).save(any(MyUser.class));

		assertEquals("redirect:/registration", result);
		assertEquals("Vous avez déjà un compte avec cette adresse email.", redirectAttributes.getFlashAttributes().get("error"));
	}
}
