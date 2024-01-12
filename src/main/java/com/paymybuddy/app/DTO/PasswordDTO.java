package com.paymybuddy.app.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PasswordDTO {

	@NotNull(message = "Veuillez fournir un mot de passe")
	@NotBlank(message = "Veuillez fournir un mot de passe")
	@Size(min = 7, message = "Le mot de passe doit contenir au moins 7 caractères")
	private String newPassword;

	@NotNull(message = "Veuillez fournir un mot de passe")
	@NotBlank(message = "Veuillez fournir un mot de passe")
	@Size(min = 7, message = "Le mot de passe doit contenir au moins 7 caractères")
	private String oldPassword;

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}
}
