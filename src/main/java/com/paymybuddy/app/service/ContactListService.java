package com.paymybuddy.app.service;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.paymybuddy.app.DTO.ContactDTO;
import com.paymybuddy.app.model.MyUser;
import com.paymybuddy.app.repository.MyUserRepository;

@Service
public class ContactListService {

	private static final Logger logger = LogManager.getLogger(ContactListService.class);

	@Autowired
	private MyUserRepository myUserRepository;
	
	@Transactional
	public String addOrDelContact(ContactDTO contactDTO, Boolean addOrDel, RedirectAttributes redirectAttributes)
			throws Exception {

		String contact = contactDTO.getContact();

		Optional<MyUser> optionalMyUser = myUserRepository.findByEmail(contact);
		if (!optionalMyUser.isPresent()) {
			redirectAttributes.addFlashAttribute("error", "Contact introuvable");
			return "redirect:/contactList";
		}

		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			String username = authentication.getName();
			MyUser myUser = myUserRepository.findByEmail(username).get();

			if (contact.equals(username)) {
				redirectAttributes.addFlashAttribute("error", "Vous ne pouvez pas vous ajouter vous-même");
				return "redirect:/contactList";
			}

			List<String> contactList = myUser.getContactList();

			if (addOrDel) {
				if (contactList.contains(contact)) {
					redirectAttributes.addFlashAttribute("error", "Le contact est déjà dans la liste");
					return "redirect:/contactList";
				} else {
					contactList.add(contact);
					redirectAttributes.addFlashAttribute("success", "Contact ajouté");
				}
			} else {
				if (contactList.contains(contact)) {
					contactList.remove(contact);
					redirectAttributes.addFlashAttribute("success", "Contact supprimé");
				} else {
					redirectAttributes.addFlashAttribute("error", "Le contact n'est pas dans la liste");
					return "redirect:/contactList";
				}
			}

			myUser.setContactList(contactList);

			myUserRepository.save(myUser);

		} catch (Exception e) {
			logger.error("Error adding contact", e);
			throw new Exception("Error adding contact", e);
		}

		return "redirect:/contactList";
	}

	@Transactional(readOnly = true)
	public String getContactList(Model model) throws Exception {

		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			MyUser myUser = myUserRepository.findByEmail(authentication.getName()).get();
			List<String> contactList = myUser.getContactList();

			model.addAttribute("contactDTO", new ContactDTO());
			model.addAttribute("contactList", contactList);
		} catch (Exception e) {
			logger.error("Error getting contactList", e);
			throw new Exception("Error getting contactList", e);
		}

		return "contactList";
	}
}
