package com.paymybuddy.app.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.paymybuddy.app.model.MyUser;
import com.paymybuddy.app.repository.MyUserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private static final Logger logger = LogManager.getLogger(CustomUserDetailsService.class);

	@Autowired
	private MyUserRepository myUserRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Optional<MyUser> optionalMyUser = myUserRepository.findByEmail(email);

		if (!optionalMyUser.isPresent()) {
			logger.error("User not found");
			throw new UsernameNotFoundException("User not found");
		}
		MyUser myUser = optionalMyUser.get();
		return new User(myUser.getEmail(), myUser.getPassword(), getGrantedAuthorities(myUser.getRole()));
	}

	private List<GrantedAuthority> getGrantedAuthorities(UserRole userRole) {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority("ROLE_" + userRole));
		return authorities;
	}
}