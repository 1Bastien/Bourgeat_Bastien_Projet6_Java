package com.paymybuddy.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import com.paymybuddy.app.DTO.PasswordDTO;
import com.paymybuddy.app.DTO.UserDTO;
import com.paymybuddy.app.mapper.MyUserMapper;
import com.paymybuddy.app.model.MyUser;
import com.paymybuddy.app.repository.MyUserRepository;

@ExtendWith(MockitoExtension.class)
public class UserAccountServiceTest {

	@InjectMocks
	private UserAccountService userAccountService;

	@Mock
	private Model model;

	@Mock
	private Authentication authentication;

	@Mock
	private MyUserMapper myUserMapper;

	@Mock
	private MyUserRepository myUserRepository;
	
	@Mock
	private PasswordEncoder passwordEncoder;

	@Test
	void testGetAccount() throws Exception {
		MyUser myUser = new MyUser();
		myUser.setEmail("test@test.com");

		SecurityContextHolder.getContext().setAuthentication(authentication);
		when(authentication.getName()).thenReturn("test@test.com");

		when(myUserRepository.findByEmail("test@test.com")).thenReturn(Optional.of(myUser));

		String viewName = userAccountService.getAccount(model);

		verify(model, times(1)).addAttribute("user", myUserMapper.myUserToUserDTO(myUser));

		assertEquals("userAccount", viewName);
	}

	@Test
	void testPostAccount() throws Exception {
		UserDTO updatedUserDTO = new UserDTO();
		updatedUserDTO.setFirstName("John");
		updatedUserDTO.setLastName("Doe");

		MyUser existingUser = new MyUser();
		existingUser.setEmail("test@example.com");

		SecurityContextHolder.getContext().setAuthentication(authentication);
		when(authentication.getName()).thenReturn("test@example.com");
		when(myUserRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));

		RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

		String result = userAccountService.postAccount(updatedUserDTO, redirectAttributes);

		verify(myUserRepository, times(1)).save(existingUser);
		verify(myUserMapper, times(1)).updateMyUserFromDTO(updatedUserDTO, existingUser);

		assertEquals("redirect:/userAccount", result);
		assertEquals("Mis à jour effectuée", redirectAttributes.getFlashAttributes().get("success"));
	}
	
	@Test
    void testChangePassword_Success() throws Exception {
        PasswordDTO passwordDTO = new PasswordDTO();
        passwordDTO.setOldPassword("oldPassword");
        passwordDTO.setNewPassword("newPassword");

        MyUser existingUser = new MyUser();
        existingUser.setEmail("test@example.com");
        existingUser.setPassword(passwordEncoder.encode("oldPassword"));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        when(myUserRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("oldPassword", existingUser.getPassword())).thenReturn(true);

        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        
        String result = userAccountService.changePassword(passwordDTO, redirectAttributes);

        verify(myUserRepository, times(1)).save(existingUser);
        verify(myUserMapper, times(1)).updateMyUserFromDTO(passwordEncoder.encode("newPassword"), existingUser);

        assertEquals("redirect:/userAccount", result);
        assertEquals("Mot de passe mis à jour", redirectAttributes.getFlashAttributes().get("success"));
    }

    @Test
    void testChangePassword_IncorrectOldPassword() throws Exception {
        PasswordDTO passwordDTO = new PasswordDTO();
        passwordDTO.setOldPassword("incorrectPassword");
        passwordDTO.setNewPassword("newPassword");

        MyUser existingUser = new MyUser();
        existingUser.setEmail("test@example.com");
        existingUser.setPassword(passwordEncoder.encode("oldPassword"));
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        when(myUserRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("incorrectPassword", existingUser.getPassword())).thenReturn(false);

        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        
        String result = userAccountService.changePassword(passwordDTO, redirectAttributes);

        verify(myUserRepository, never()).save(existingUser);
        verify(myUserMapper, never()).updateMyUserFromDTO(any(UserDTO.class), any());

        assertEquals("redirect:/userAccount", result);
        assertEquals("Mot de passe incorrect", redirectAttributes.getFlashAttributes().get("error"));
    }
}
