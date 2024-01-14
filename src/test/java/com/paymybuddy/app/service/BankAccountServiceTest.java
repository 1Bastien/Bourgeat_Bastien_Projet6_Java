package com.paymybuddy.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;

import java.math.BigDecimal;
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

import com.paymybuddy.app.DTO.AmountDTO;
import com.paymybuddy.app.DTO.UserDTO;
import com.paymybuddy.app.mapper.MyUserMapper;
import com.paymybuddy.app.model.MyUser;
import com.paymybuddy.app.repository.MyUserRepository;

@ExtendWith(MockitoExtension.class)
public class BankAccountServiceTest {

	@InjectMocks
	private BankAccountService bankAccountService;

	@Mock
	private BankConnectionService bankConnectionService;

	@Mock
	private MyUserRepository myUserRepository;

	@Mock
	private MyUserMapper myUserMapper;

	@Mock
	private Model model;

	@Mock
	private Authentication authentication;

	@Test
	void testShowReloadAccountPage() {
		MyUser myUser = new MyUser();
		myUser.setEmail("test@example.com");
		myUser.setBalance(new BigDecimal(100));

		when(myUserRepository.findByEmail(anyString())).thenReturn(Optional.of(myUser));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		when(authentication.getName()).thenReturn("test@example.com");

		String viewName = bankAccountService.showReloadAccountPage(model);

		verify(myUserRepository, times(1)).findByEmail(anyString());

		verify(model, times(1)).addAttribute(eq("amountDTO"), any(AmountDTO.class));
		verify(model, times(1)).addAttribute(eq("balance"), eq(new BigDecimal(100)));

		assertEquals("reloadAccount", viewName);
	}

	@Test
	void testshowTransferToBankPage() {
		MyUser myUser = new MyUser();
		myUser.setEmail("test@exemple.fr");
		myUser.setBalance(new BigDecimal(100));

		when(myUserRepository.findByEmail(anyString())).thenReturn(Optional.of(myUser));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		when(authentication.getName()).thenReturn("test@exemple.com");

		String viewName = bankAccountService.showTransferToBankPage(model);

		verify(myUserRepository, times(1)).findByEmail(anyString());

		verify(model, times(1)).addAttribute(eq("amountDTO"), any(AmountDTO.class));
		verify(model, times(1)).addAttribute(eq("balance"), eq(new BigDecimal(100)));

		assertEquals("transferToBank", viewName);
	}

	@Test
	public void testTransferToBank_SuccessfulTransfer() throws Exception {
		AmountDTO amountDTO = new AmountDTO();
		amountDTO.setAmount(new BigDecimal(50));

		MyUser myUser = new MyUser();
		myUser.setEmail("test@exemple.com");
		myUser.setBalance(new BigDecimal(100));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		when(authentication.getName()).thenReturn("test@exemple.com");

		when(myUserRepository.findByEmail(anyString())).thenReturn(Optional.of(myUser));

		when(bankConnectionService.transferToBankAccount(eq(myUser), any(BigDecimal.class))).thenReturn(true);

		String result = bankAccountService.tranferToBank(amountDTO, new RedirectAttributesModelMap());

		verify(myUserRepository, times(1)).findByEmail(anyString());
		verify(bankConnectionService, times(1)).transferToBankAccount(eq(myUser), any(BigDecimal.class));
		verify(myUserMapper, times(1)).updateMyUserFromDTO(myUser.getBalance().subtract(amountDTO.getAmount()), myUser);
		verify(myUserRepository, times(1)).save(eq(myUser));

		assertEquals("redirect:/transferToBank", result);
	}

	@Test
	public void testTransferToBank_InsufficientBalance() throws Exception {
		AmountDTO amountDTO = new AmountDTO();
		amountDTO.setAmount(new BigDecimal(150));

		MyUser myUser = new MyUser();
		myUser.setEmail("john@test.com");
		myUser.setBalance(new BigDecimal(100));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		when(authentication.getName()).thenReturn("test@exemple.com");

		when(myUserRepository.findByEmail(anyString())).thenReturn(Optional.of(myUser));

		String result = bankAccountService.tranferToBank(amountDTO, new RedirectAttributesModelMap());

		verify(myUserRepository, times(1)).findByEmail(anyString());
		verify(bankConnectionService, never()).transferToBankAccount(any(), any());
		verify(myUserMapper, never()).updateMyUserFromDTO(any(UserDTO.class), any());
		verify(myUserRepository, never()).save(any());

		assertEquals("redirect:/transferToBank", result);
	}

	@Test
	public void testTransferToBank_FailedTransfer() throws Exception {
		AmountDTO amountDTO = new AmountDTO();
		amountDTO.setAmount(new BigDecimal(50));

		MyUser myUser = new MyUser();
		myUser.setEmail("john@test.com");
		myUser.setBalance(new BigDecimal(100));

		when(myUserRepository.findByEmail(anyString())).thenReturn(Optional.of(myUser));

		when(bankConnectionService.transferToBankAccount(eq(myUser), any(BigDecimal.class))).thenReturn(false);

		String result = bankAccountService.tranferToBank(amountDTO, new RedirectAttributesModelMap());

		verify(myUserRepository, times(1)).findByEmail(anyString());
		verify(bankConnectionService, times(1)).transferToBankAccount(eq(myUser), any(BigDecimal.class));
		verify(myUserMapper, never()).updateMyUserFromDTO(any(UserDTO.class), any());
		verify(myUserRepository, never()).save(any());

		assertEquals("redirect:/transferToBank", result);
	}

	@Test
	void testReloadAccount_SuccessfulTransfer() throws Exception {
		AmountDTO amountDTO = new AmountDTO();
		amountDTO.setAmount(new BigDecimal(50));

		MyUser myUser = new MyUser();
		myUser.setEmail("test@example.com");
		myUser.setBalance(new BigDecimal(100));

		when(myUserRepository.findByEmail(anyString())).thenReturn(Optional.of(myUser));
		when(bankConnectionService.transferFromBankAccount(eq(myUser), any(BigDecimal.class))).thenReturn(true);

		String result = bankAccountService.reloadAccount(amountDTO, new RedirectAttributesModelMap());

		verify(myUserRepository, times(1)).findByEmail(anyString());
		verify(bankConnectionService, times(1)).transferFromBankAccount(eq(myUser), any(BigDecimal.class));
		verify(myUserMapper, times(1)).updateMyUserFromDTO(myUser.getBalance().add(amountDTO.getAmount()), myUser);
		verify(myUserRepository, times(1)).save(eq(myUser));

		assertEquals("redirect:/reloadAccount", result);
	}

	@Test
	void testReloadAccount_FailedTransfer() throws Exception {
		AmountDTO amountDTO = new AmountDTO();
		amountDTO.setAmount(new BigDecimal(50));

		MyUser myUser = new MyUser();
		myUser.setEmail("test@example.com");
		myUser.setBalance(new BigDecimal(100));

		when(myUserRepository.findByEmail(anyString())).thenReturn(Optional.of(myUser));
		when(bankConnectionService.transferFromBankAccount(eq(myUser), any(BigDecimal.class))).thenReturn(false);

		String result = bankAccountService.reloadAccount(amountDTO, new RedirectAttributesModelMap());

		verify(myUserRepository, times(1)).findByEmail(anyString());
		verify(bankConnectionService, times(1)).transferFromBankAccount(eq(myUser), any(BigDecimal.class));
		verify(myUserMapper, never()).updateMyUserFromDTO(any(UserDTO.class), any());
		verify(myUserRepository, never()).save(any());

		assertEquals("redirect:/reloadAccount", result);
	}

}
