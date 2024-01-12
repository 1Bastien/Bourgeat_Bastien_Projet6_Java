package com.paymybuddy.app.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.paymybuddy.app.DTO.TransferDTO;
import com.paymybuddy.app.model.MyUser;
import com.paymybuddy.app.model.Transaction;
import com.paymybuddy.app.repository.MyUserRepository;
import com.paymybuddy.app.repository.TransactionRepository;

@Service
public class TransactionService {

	private static final Logger logger = LogManager.getLogger(TransactionService.class);

	private MyUserRepository myUserRepository;

	private TransactionRepository transactionRepository;

	private BillingTransactionService billingTransactionService;

	public TransactionService(MyUserRepository myUserRepository, TransactionRepository transactionRepository,
			BillingTransactionService billingTransactionService) {
		this.myUserRepository = myUserRepository;
		this.transactionRepository = transactionRepository;
		this.billingTransactionService = billingTransactionService;
	}

	@Transactional(readOnly = true)
	public String getTransaction(Model model) throws Exception {

		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			MyUser myUser = myUserRepository.findByEmail(authentication.getName()).get();
			List<Transaction> transactionList = new ArrayList<Transaction>();

			List<Transaction> transactionSenderList = transactionRepository.findBySender(myUser);
			transactionList.addAll(transactionSenderList);

			List<Transaction> transactionReceiverList = transactionRepository.findByReceiver(myUser);
			transactionList.addAll(transactionReceiverList);

			List<Transaction> sortedTransactionList = transactionList.stream()
					.sorted(Comparator.comparing(Transaction::getTransactionDate).reversed())
					.collect(Collectors.toList());

			model.addAttribute("transferDTO", new TransferDTO());
			model.addAttribute("balance", myUser.getBalance());
			model.addAttribute("transactionList", sortedTransactionList);
			model.addAttribute("contactList", myUser.getContactList());
		} catch (Exception e) {
			logger.error("Error getting transacationList", e);
			throw new Exception("Error getting transactionList", e);
		}

		return "transfer";
	}

	@Transactional
	public String transferToContact(TransferDTO transferDTO, RedirectAttributes redirectAttributes) throws Exception {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			MyUser myUser = myUserRepository.findByEmail(authentication.getName()).get();
			List<String> contactList = myUser.getContactList();

			if (!contactList.contains(transferDTO.getContact())) {
				redirectAttributes.addFlashAttribute("error", "Le contact n'existe pas");
				return "redirect:/transfer";
			}

			if (transferDTO.getAmount().compareTo(myUser.getBalance()) > 0) {
				redirectAttributes.addFlashAttribute("error", "Le montant est supérieur au solde du compte");
				return "redirect:/transfer";
			}

			MyUser contact = myUserRepository.findByEmail(transferDTO.getContact()).get();

			Transaction transaction = new Transaction();
			transaction.setAmount(transferDTO.getAmount());
			transaction.setSender(myUser);
			transaction.setReceiver(contact);
			transaction.setFee(billingTransactionService.fee(transaction.getAmount()));

			LocalDateTime localDateTime = LocalDateTime.now();
			transaction.setTransactionDate(localDateTime);

			transactionRepository.save(transaction);

			BigDecimal amountWithFee = billingTransactionService.amountWithFee(transaction.getAmount());

			myUser.setBalance(myUser.getBalance().subtract(amountWithFee));
			contact.setBalance(contact.getBalance().add(transaction.getAmount()));

			myUserRepository.save(myUser);
			myUserRepository.save(contact);

		} catch (Exception e) {
			logger.error("Error transfering to contact", e);
			throw new Exception("Error transfering to contact", e);
		}
		return "redirect:/transfer";
	}
}
