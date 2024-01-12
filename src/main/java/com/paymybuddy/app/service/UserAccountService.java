package com.paymybuddy.app.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.paymybuddy.app.DTO.PasswordDTO;
import com.paymybuddy.app.DTO.UserDTO;
import com.paymybuddy.app.mapper.MyUserMapper;
import com.paymybuddy.app.model.MyUser;
import com.paymybuddy.app.repository.MyUserRepository;

@Service
public class UserAccountService {

	private static final Logger logger = LogManager.getLogger(UserAccountService.class);

	private MyUserRepository myUserRepository;

	private PasswordEncoder passwordEncoder;

	private MyUserMapper myUserMapper;

	public UserAccountService(MyUserRepository myUserRepository, PasswordEncoder passwordEncoder,
			MyUserMapper myUserMapper) {
		this.myUserRepository = myUserRepository;
		this.passwordEncoder = passwordEncoder;
		this.myUserMapper = myUserMapper;
	}

	@Transactional(readOnly = true)
	public String getAccount(Model model) throws Exception {

		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			MyUser myUser = myUserRepository.findByEmail(authentication.getName()).get();

			UserDTO user = myUserMapper.myUserToUserDTO(myUser);
			model.addAttribute("user", user);

			PasswordDTO passwordDTO = new PasswordDTO();
			model.addAttribute("passwordDTO", passwordDTO);

		} catch (Exception e) {
			logger.error("Error getting user", e);
			throw new Exception("Error getting user", e);
		}

		return "userAccount";
	}

	@Transactional
	public String postAccount(UserDTO user, RedirectAttributes redirectAttributes) throws Exception {

		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			MyUser myUser = myUserRepository.findByEmail(authentication.getName()).get();

			myUserMapper.updateMyUserFromDTO(user, myUser);

			myUserRepository.save(myUser);

			redirectAttributes.addFlashAttribute("success", "Mis à jour effectuée");

		} catch (Exception e) {
			logger.error("Error updating user", e);
			throw new Exception("Error updating user", e);
		}

		return "redirect:/userAccount";
	}

	@Transactional
	public String changePassword(PasswordDTO passwordDTO, RedirectAttributes redirectAttributes) throws Exception {

		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			MyUser myUser = myUserRepository.findByEmail(authentication.getName()).get();

			if (passwordEncoder.matches(passwordDTO.getOldPassword(), myUser.getPassword())) {

				myUserMapper.updateMyUserFromDTO(passwordEncoder.encode(passwordDTO.getNewPassword()), myUser);
				myUserRepository.save(myUser);

				redirectAttributes.addFlashAttribute("success", "Mot de passe mis à jour");

			} else {
				redirectAttributes.addFlashAttribute("error", "Mot de passe incorrect");
			}
		} catch (

		Exception e) {
			logger.error("Error updating password", e);
			throw new Exception("Error updating password", e);
		}

		return "redirect:/userAccount";
	}
}
