package com.paymybuddy.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.paymybuddy.app.DTO.AmountDTO;
import com.paymybuddy.app.service.BankAccountService;

import jakarta.validation.Valid;

@Controller
public class BankAccountController {

	@Autowired
	private BankAccountService bankAccountService;

	@GetMapping("/reloadAccount")
	public String reloadAccount(Model model) {
		return bankAccountService.showReloadAccountPage(model);
	}

	@PostMapping("/reloadAccount")
	public String reloadAccount(@Valid @ModelAttribute("amountDTO") AmountDTO amountDTO, BindingResult result,
			RedirectAttributes redirectAttributes) throws Exception {
		if (result.hasErrors()) {
			return "reloadAccount";
		}
		return bankAccountService.reloadAccount(amountDTO, redirectAttributes);
	}

	@GetMapping("/transferToBank")
	public String transferToBank(Model model) {
		return bankAccountService.showTransferToBankPage(model);
	}

	@PostMapping("/transferToBank")
	public String transferToBank(@Valid @ModelAttribute("amountDTO") AmountDTO amountDTO, BindingResult result,
			RedirectAttributes redirectAttributes) throws Exception {
		if (result.hasErrors()) {
			return "transferToBank";
		}
		return bankAccountService.tranferToBank(amountDTO, redirectAttributes);
	}
}
