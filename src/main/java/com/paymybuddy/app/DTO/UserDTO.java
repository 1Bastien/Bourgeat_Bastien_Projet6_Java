package com.paymybuddy.app.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserDTO {

	@NotNull
	@NotBlank(message = "Veuillez fournir un prénom")
	@Size(min = 3, max = 30, message = "Le prénom doit contenir entre 3 et 30 caractères")
	private String firstName;

	@NotNull
	@NotBlank(message = "Veuillez fournir un nom")
	@Size(min = 3, max = 30, message = "Le nom doit contenir entre 3 et 30 caractères")
	private String lastName;

	@NotNull
	@NotBlank(message = "Veuillez fournir une adresse de facturation")
	@Size(min = 3, max = 150, message = "L'adresse de facturation doit contenir entre 3 et 150 caractères")
	private String billingAddress;

	@Size(max = 30, message = "Le numéro de compte bancaire doit contenir au maximum 30 caractères")
	private String bankAccountNumber;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getBillingAddress() {
		return billingAddress;
	}

	public void setBillingAddress(String billingAddress) {
		this.billingAddress = billingAddress;
	}

	public String getBankAccountNumber() {
		return bankAccountNumber;
	}

	public void setBankAccountNumber(String bankAccountNumber) {
		this.bankAccountNumber = bankAccountNumber;
	}
}
