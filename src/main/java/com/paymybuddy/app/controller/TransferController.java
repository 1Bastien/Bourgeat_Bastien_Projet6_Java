package com.paymybuddy.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.paymybuddy.app.DTO.TransferDTO;
import com.paymybuddy.app.service.TransactionService;

import jakarta.validation.Valid;

@Controller
public class TransferController {

	@Autowired
	private TransactionService transactionService;

	@GetMapping("/transfer")
	public String transfer(Model model) throws Exception {
		return transactionService.getTransaction(model);
	}

	@PostMapping("/transfer")
	public String transfer(@Valid @ModelAttribute("transferDTO") TransferDTO transferDTO, BindingResult result,
			RedirectAttributes redirectAttributes) throws Exception {
		if (result.hasErrors()) {
			return "transfer";
		}
		return transactionService.transferToContact(transferDTO, redirectAttributes);
	}
}
