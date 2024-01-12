package com.paymybuddy.app.service;

import java.math.BigDecimal;

import com.paymybuddy.app.model.MyUser;

public interface BankConnectionService {

	Boolean transferToBankAccount(MyUser user, BigDecimal amount);

	Boolean transferFromBankAccount(MyUser user, BigDecimal amount);
}
