package com.paymybuddy.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.paymybuddy.app.model.MyUser;
import com.paymybuddy.app.service.RegistrationService;

import jakarta.validation.Valid;

@Controller
public class RegistrationController {

	@Autowired
	private RegistrationService registrationService;

	@GetMapping("/registration")
	public String showRegistrationForm(Model model) throws Exception {
		return registrationService.showRegistrationForm(model);
	}

	@PostMapping("/registration")
	public String processRegistration(@Valid @ModelAttribute("user") MyUser user, BindingResult result,
			RedirectAttributes redirectAttributes) throws Exception {

		if (result.hasErrors()) {
			return "registration";
		}
		return registrationService.processRegistration(user, redirectAttributes);
	}
}