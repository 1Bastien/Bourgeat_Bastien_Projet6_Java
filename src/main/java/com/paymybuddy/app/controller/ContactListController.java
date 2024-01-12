package com.paymybuddy.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.paymybuddy.app.DTO.ContactDTO;
import com.paymybuddy.app.service.ContactListService;

import jakarta.validation.Valid;

@Controller
public class ContactListController {

	@Autowired
	private ContactListService contactListService;

	@GetMapping("/contactList")
	public String getContactList(Model model) throws Exception {
		return contactListService.getContactList(model);
	}

	@PostMapping("/contactList")
	public String postAccountContactList(@Valid @ModelAttribute("contactDTO") ContactDTO contactDTO,
			BindingResult result, @RequestParam("addOrDel") Boolean addOrDel, RedirectAttributes redirectAttributes)
			throws Exception {

		if (result.hasErrors()) {
			return "contactList";
		}

		return contactListService.addOrDelContact(contactDTO, addOrDel, redirectAttributes);
	}
}
