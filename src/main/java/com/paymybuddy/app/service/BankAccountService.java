package com.paymybuddy.app.service;

import java.math.BigDecimal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.paymybuddy.app.DTO.AmountDTO;
import com.paymybuddy.app.model.MyUser;
import com.paymybuddy.app.repository.MyUserRepository;

@Service
public class BankAccountService {

	private static final Logger logger = LogManager.getLogger(BankAccountService.class);

	private BankConnectionService bankConnectionService;

	private MyUserRepository myUserRepository;

	public BankAccountService(BankConnectionService bankConnectionService, MyUserRepository myUserRepository) {
		this.bankConnectionService = bankConnectionService;
		this.myUserRepository = myUserRepository;
	}

	@Transactional(readOnly = true)
	public String showReloadAccountPage(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		MyUser myUser = myUserRepository.findByEmail(authentication.getName()).get();

		model.addAttribute("amountDTO", new AmountDTO());
		model.addAttribute("balance", myUser.getBalance());

		return "reloadAccount";
	}

	@Transactional(readOnly = true)
	public String showTransferToBankPage(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		MyUser myUser = myUserRepository.findByEmail(authentication.getName()).get();

		model.addAttribute("amountDTO", new AmountDTO());
		model.addAttribute("balance", myUser.getBalance());

		return "transferToBank";
	}

	@Transactional
	public String tranferToBank(AmountDTO amountDTO, RedirectAttributes redirectAttributes) throws Exception {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			MyUser myUser = myUserRepository.findByEmail(authentication.getName()).get();

			if (amountDTO.getAmount().compareTo(myUser.getBalance()) > 0) {
				redirectAttributes.addFlashAttribute("error", "Le montant est supérieur au solde du compte");
				return "redirect:/transferToBank";
			}

			BigDecimal amount = amountDTO.getAmount();

			if (!bankConnectionService.transferToBankAccount(myUser, amount)) {
				redirectAttributes.addFlashAttribute("error", "La banque a refusé le transfert");
				return "redirect:/transferToBank";
			}

			myUser.setBalance(myUser.getBalance().subtract(amount));
			myUserRepository.save(myUser);

			redirectAttributes.addFlashAttribute("success", "Le montant a été transféré sur votre compte bancaire");

		} catch (Exception e) {
			logger.error("Error tranfer to bank", e);
			throw new Exception("Error tranfer to bank", e);
		}
		return "redirect:/transferToBank";
	}

	@Transactional
	public String reloadAccount(AmountDTO amountDTO, RedirectAttributes redirectAttributes) throws Exception {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			MyUser myUser = myUserRepository.findByEmail(authentication.getName()).get();

			BigDecimal amount = amountDTO.getAmount();

			if (!bankConnectionService.transferFromBankAccount(myUser, amount)) {
				redirectAttributes.addFlashAttribute("error", "La banque a refusé le transfert");
				return "redirect:/reloadAccount";
			}

			myUser.setBalance(myUser.getBalance().add(amount));
			myUserRepository.save(myUser);

			redirectAttributes.addFlashAttribute("success", "Le montant a été transféré sur votre compte PayMyBuddy");

		} catch (Exception e) {
			logger.error("Error reload account", e);
			throw new Exception("Error reload account", e);
		}
		return "redirect:/reloadAccount";
	}
}
