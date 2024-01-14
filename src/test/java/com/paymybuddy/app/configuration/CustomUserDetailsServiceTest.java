package com.paymybuddy.app.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.paymybuddy.app.model.MyUser;
import com.paymybuddy.app.repository.MyUserRepository;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

	@InjectMocks
	private CustomUserDetailsService customUserDetailsService;

	@Mock
	private MyUserRepository myUserRepository;

	@Test
	void testLoadUserByUsername_UserFound() {
		String userEmail = "test@example.com";

		MyUser myUser = new MyUser();
		myUser.setEmail(userEmail);
		myUser.setPassword("encodedPassword");
		myUser.setRole(UserRole.USER);

		when(myUserRepository.findByEmail(userEmail)).thenReturn(Optional.of(myUser));

		UserDetails userDetails = customUserDetailsService.loadUserByUsername(userEmail);

		assertNotNull(userDetails);
		assertEquals(userEmail, userDetails.getUsername());
		assertEquals("encodedPassword", userDetails.getPassword());

		Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
		assertEquals(1, authorities.size());
		assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
	}

	@Test
	void testLoadUserByUsername_UserNotFound() {
		String userEmail = "nonexistent@example.com";

		when(myUserRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

		assertThrows(UsernameNotFoundException.class, () -> {
			customUserDetailsService.loadUserByUsername(userEmail);
		});
	}
}
