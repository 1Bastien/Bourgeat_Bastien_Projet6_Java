package com.paymybuddy.app.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.paymybuddy.app.configuration.SpringSecurityConfig;
import com.paymybuddy.app.model.MyUser;
import com.paymybuddy.app.repository.MyUserRepository;

@Service
public class RegistrationService {

	private static final Logger logger = LoggerFactory.getLogger(SpringSecurityConfig.class);

	private MyUserRepository myUserRepository;

	private PasswordEncoder passwordEncoder;

	public RegistrationService(MyUserRepository myUserRepository, PasswordEncoder passwordEncoder) {
		this.myUserRepository = myUserRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public String showRegistrationForm(Model model) throws Exception {
		try {
			model.addAttribute("user", new MyUser());
		} catch (Exception e) {
			logger.error("Error showing registration form", e);
			throw new Exception("Error showing registration form", e);
		}
		return "registration";
	}

	@Transactional
	public String processRegistration(MyUser user, RedirectAttributes redirectAttributes) throws Exception {

		Optional<MyUser> optionalMyUser = myUserRepository.findByEmail(user.getEmail());
		if (optionalMyUser.isPresent()) {
			redirectAttributes.addFlashAttribute("error", "Vous avez déjà un compte avec cette adresse email.");
			return "redirect:/registration";
		}

		try {
			user.setPassword(passwordEncoder.encode(user.getPassword()));

			myUserRepository.save(user);
		} catch (Exception e) {
			logger.error("Error saving user", e);
			throw new Exception("Error saving user", e);
		}

		return "redirect:/login";
	}
}
