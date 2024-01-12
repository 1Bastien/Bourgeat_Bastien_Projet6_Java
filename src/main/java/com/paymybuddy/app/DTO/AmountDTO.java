package com.paymybuddy.app.DTO;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;

public class AmountDTO {

	@DecimalMin(value = "0.00", inclusive = false, message = "Le montant doit être positif")
	@DecimalMax(value = "1000000.00", inclusive = true, message = "Le montant maximal autorisé est 1,000,000.00")
	@Digits(integer = 8, fraction = 2, message = "Le montant doit avoir au maximum 8 chiffres avant la virgule et 2 chiffres après la virgule")
	private BigDecimal amount;

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
}
