package com.paymybuddy.app.service.impl;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.paymybuddy.app.model.MyUser;
import com.paymybuddy.app.model.Transaction;
import com.paymybuddy.app.service.BillingTransactionService;

@Service
public class BillingTransactionServiceImpl implements BillingTransactionService {

	@Override
	public BigDecimal amountWithFee(BigDecimal amount) {

		BigDecimal fee = amount.multiply(new BigDecimal("0.05"));
		BigDecimal amountWithFee = amount.add(fee);

		return amountWithFee;
	}
	
	@Override
	public BigDecimal fee(BigDecimal amount) {

		BigDecimal fee = amount.multiply(new BigDecimal("0.05"));

		return fee;
	}

	@Override
	public void billingTransaction(MyUser sender, Transaction transaction) {
	}
}
