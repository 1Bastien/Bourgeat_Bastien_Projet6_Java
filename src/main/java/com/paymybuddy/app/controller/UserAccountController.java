package com.paymybuddy.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.paymybuddy.app.DTO.PasswordDTO;
import com.paymybuddy.app.DTO.UserDTO;
import com.paymybuddy.app.service.UserAccountService;

import jakarta.validation.Valid;

@Controller
public class UserAccountController {

	@Autowired
	private UserAccountService accountService;

	@GetMapping("/userAccount")
	public String getAccount(Model model) throws Exception {
		return accountService.getAccount(model);
	}

	@PostMapping("/userAccount")
	public String postAccount(@Valid @ModelAttribute("user") UserDTO user, BindingResult result,
			RedirectAttributes redirectAttributes) throws Exception {
		if (result.hasErrors()) {
			return "userAccount";
		}

		return accountService.postAccount(user, redirectAttributes);
	}

	@PostMapping("/userAccount/password")
	public String postPassword(@Valid @ModelAttribute("passwordDTO") PasswordDTO passwordDTO, BindingResult result,
			RedirectAttributes redirectAttributes) throws Exception {

		if (result.hasErrors()) {
			redirectAttributes.addFlashAttribute("error", "Le mot de passe n'a pas été modifié");

			return "redirect:/userAccount";
		}

		return accountService.changePassword(passwordDTO, redirectAttributes);
	}
}