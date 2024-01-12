package com.paymybuddy.app.service.impl;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.paymybuddy.app.model.MyUser;
import com.paymybuddy.app.service.BankConnectionService;

@Service
public class BankConnectionServiceImpl implements BankConnectionService {

	@Override
	public Boolean transferToBankAccount(MyUser user, BigDecimal amount) {
		// TODO
		return true;
	}

	@Override
	public Boolean transferFromBankAccount(MyUser user, BigDecimal amount) {
		// TODO
		return true;
	}
}
