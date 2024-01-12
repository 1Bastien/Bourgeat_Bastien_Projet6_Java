package com.paymybuddy.app.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class ContactDTO {

	@Email(message = "Veuillez fournir une adresse e-mail valide")
	@NotEmpty(message = "Veuillez fournir une adresse e-mail")
	@NotNull(message = "Veuillez fournir une adresse e-mail")
	private String contact;

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}
}
