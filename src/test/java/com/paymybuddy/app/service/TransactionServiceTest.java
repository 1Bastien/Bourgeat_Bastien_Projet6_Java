package com.paymybuddy.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import com.paymybuddy.app.DTO.TransferDTO;
import com.paymybuddy.app.model.MyUser;
import com.paymybuddy.app.model.Transaction;
import com.paymybuddy.app.repository.MyUserRepository;
import com.paymybuddy.app.repository.TransactionRepository;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

	@InjectMocks
	private TransactionService transactionService;

	@Mock
	private Model model;

	@Mock
	private Authentication authentication;

	@Mock
	private MyUserRepository myUserRepository;

	@Mock
	private TransactionRepository transactionRepository;

	@Mock
	private BillingTransactionService billingTransactionService;

	@Test
	void testGetTransaction() throws Exception {
		MyUser myUser = new MyUser();
		myUser.setEmail("test@example.com");
		myUser.setBalance(new BigDecimal(100));

		Transaction transaction1 = new Transaction();
		transaction1.setSender(myUser);
		transaction1.setReceiver(new MyUser());
		transaction1.setTransactionDate(LocalDateTime.now());

		Transaction transaction2 = new Transaction();
		transaction2.setSender(new MyUser());
		transaction2.setReceiver(myUser);
		transaction2.setTransactionDate(LocalDateTime.now().minusDays(1));

		List<Transaction> transactionList = Arrays.asList(transaction1, transaction2);

		SecurityContextHolder.getContext().setAuthentication(authentication);
		when(authentication.getName()).thenReturn("test@example.com");

		when(myUserRepository.findByEmail("test@example.com")).thenReturn(Optional.of(myUser));
		when(transactionRepository.findBySender(myUser)).thenReturn(Arrays.asList(transaction1));
		when(transactionRepository.findByReceiver(myUser)).thenReturn(Arrays.asList(transaction2));

		String result = transactionService.getTransaction(model);

		verify(myUserRepository, times(1)).findByEmail(eq("test@example.com"));
		verify(transactionRepository, times(1)).findBySender(eq(myUser));
		verify(transactionRepository, times(1)).findByReceiver(eq(myUser));

		verify(model, times(1)).addAttribute(eq("transferDTO"), any(TransferDTO.class));
		verify(model, times(1)).addAttribute(eq("balance"), eq(new BigDecimal(100)));
		verify(model, times(1)).addAttribute(eq("transactionList"), eq(transactionList));
		verify(model, times(1)).addAttribute(eq("contactList"), any(List.class));

		assertEquals("transfer", result);
	}

	@Test
	void testTransferToContact_SuccessfulTransfer() throws Exception {
		MyUser contact = new MyUser();
		contact.setEmail("contact@example.com");
		contact.setBalance(new BigDecimal(50));

		List<String> contactList = new ArrayList<>();
		contactList.add("contact@example.com");
		
		MyUser myUser = new MyUser();
		myUser.setEmail("test@example.com");
		myUser.setBalance(new BigDecimal(100));
		myUser.setContactList(contactList);

		TransferDTO transferDTO = new TransferDTO();
		transferDTO.setContact("contact@example.com");
		transferDTO.setAmount(new BigDecimal(30));

		RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		when(authentication.getName()).thenReturn("test@example.com");

		when(myUserRepository.findByEmail("test@example.com")).thenReturn(Optional.of(myUser));
		when(myUserRepository.findByEmail("contact@example.com")).thenReturn(Optional.of(contact));
		when(billingTransactionService.fee(any(BigDecimal.class))).thenReturn(new BigDecimal(1));
		when(billingTransactionService.amountWithFee(any(BigDecimal.class))).thenReturn(new BigDecimal(31));

		String result = transactionService.transferToContact(transferDTO, redirectAttributes);

		verify(myUserRepository, times(1)).findByEmail(eq("test@example.com"));
		verify(myUserRepository, times(1)).findByEmail(eq("contact@example.com"));
		verify(transactionRepository, times(1)).save(any(Transaction.class));
		verify(myUserRepository, times(2)).save(any(MyUser.class));
		verify(billingTransactionService, times(1)).fee(any(BigDecimal.class));
		verify(billingTransactionService, times(1)).amountWithFee(any(BigDecimal.class));

		assertEquals("redirect:/transfer", result);
	}

	@Test
	void testTransferToContact_ContactNotExists() throws Exception {
		List<String> contactList = new ArrayList<>();
		
		MyUser myUser = new MyUser();
		myUser.setEmail("test@example.com");
		myUser.setBalance(new BigDecimal(100));
		myUser.setContactList(contactList);

		TransferDTO transferDTO = new TransferDTO();
		transferDTO.setContact("nonexistent@example.com");
		transferDTO.setAmount(new BigDecimal(30));

		RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		when(authentication.getName()).thenReturn("test@example.com");
		
		when(myUserRepository.findByEmail("test@example.com")).thenReturn(Optional.of(myUser));
		
		String result = transactionService.transferToContact(transferDTO, redirectAttributes);

		verify(myUserRepository, times(1)).findByEmail(eq("test@example.com"));
		verify(transactionRepository, never()).save(any(Transaction.class));
		verify(myUserRepository, never()).save(any(MyUser.class));
		verify(billingTransactionService, never()).fee(any(BigDecimal.class));
		verify(billingTransactionService, never()).amountWithFee(any(BigDecimal.class));

		assertEquals("redirect:/transfer", result);
		assertEquals("Le contact n'existe pas", redirectAttributes.getFlashAttributes().get("error"));
	}
	
	@Test
	void testTransferToContact_AmountUnavailable() throws Exception {
		MyUser contact = new MyUser();
		contact.setEmail("contact@example.com");
		contact.setBalance(new BigDecimal(50));

		List<String> contactList = new ArrayList<>();
		contactList.add("contact@example.com");
		
		MyUser myUser = new MyUser();
		myUser.setEmail("test@example.com");
		myUser.setBalance(new BigDecimal(50));
		myUser.setContactList(contactList);

		TransferDTO transferDTO = new TransferDTO();
		transferDTO.setContact("contact@example.com");
		transferDTO.setAmount(new BigDecimal(100));

		RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		when(authentication.getName()).thenReturn("test@example.com");

		when(myUserRepository.findByEmail("test@example.com")).thenReturn(Optional.of(myUser));

		String result = transactionService.transferToContact(transferDTO, redirectAttributes);

		verify(myUserRepository, times(1)).findByEmail(eq("test@example.com"));
		verify(myUserRepository, never()).findByEmail(eq("contact@example.com"));
		verify(transactionRepository, never()).save(any(Transaction.class));
		verify(myUserRepository, never()).save(any(MyUser.class));
		verify(billingTransactionService, never()).fee(any(BigDecimal.class));
		verify(billingTransactionService, never()).amountWithFee(any(BigDecimal.class));

		assertEquals("redirect:/transfer", result);
		assertEquals("Le montant est supérieur au solde du compte", redirectAttributes.getFlashAttributes().get("error"));
	}
}
