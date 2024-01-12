package com.paymybuddy.app.service;

import java.math.BigDecimal;

import com.paymybuddy.app.model.MyUser;
import com.paymybuddy.app.model.Transaction;

public interface BillingTransactionService {

	BigDecimal amountWithFee(BigDecimal amount);

	void billingTransaction(MyUser sender, Transaction transaction);

	BigDecimal fee(BigDecimal amount);
}
