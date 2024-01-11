package com.paymybuddy.app.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import com.paymybuddy.app.configuration.UserRole;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "my_user", uniqueConstraints = {
		@UniqueConstraint(columnNames = "email", name = "unique_email_constraint") })
public class MyUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@NotNull
	@Column(nullable = false)
	private long id;

	@NotNull
	@NotBlank(message = "Veuillez fournir un prénom")
	@Size(min = 3, max = 30, message = "Le prénom doit contenir entre 3 et 30 caractères")
	@Column(nullable = false)
	private String firstName;

	@NotNull
	@NotBlank(message = "Veuillez fournir un nom")
	@Size(min = 3, max = 30, message = "Le nom doit contenir entre 3 et 30 caractères")
	@Column(nullable = false)
	private String lastName;

	@NotNull
	@NotBlank(message = "Veuillez fournir une adresse e-mail")
	@Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,6}$", message = "Veuillez fournir une adresse e-mail valide")
	@Size(min = 3, max = 30, message = "L'adresse e-mail doit contenir entre 3 et 30 caractères")
	@Column(nullable = false, unique = true)
	private String email;

	@NotNull
	@NotBlank(message = "Veuillez fournir un mot de passe")
	@Size(min = 7, message = "Le mot de passe doit contenir au moins 7 caractères")
	@Column(nullable = false)
	private String password;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 5)
	private UserRole role = UserRole.USER;

	@Column(nullable = false, columnDefinition = "DECIMAL(10,2)")
	private BigDecimal balance = BigDecimal.valueOf(0.00);

	@NotNull
	@NotBlank(message = "Veuillez fournir une adresse de facturation")
	@Size(min = 3, max = 150, message = "L'adresse de facturation doit contenir entre 3 et 150 caractères")
	@Column(nullable = false)
	private String billingAddress;

	@Size(max = 30, message = "Le numéro de compte bancaire doit contenir au maximum 30 caractères")
	private String bankAccountNumber;

	@ElementCollection
	@CollectionTable(name = "contact_list", joinColumns = @JoinColumn(name = "user_id"))
	private List<String> contactList = new ArrayList<>();

	@OneToMany(mappedBy = "sender")
	private List<Transaction> sentTransactions;

	@OneToMany(mappedBy = "receiver")
	private List<Transaction> receivedTransactions;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public String getBillingAddress() {
		return billingAddress;
	}

	public void setBillingAddress(String billingAddress) {
		this.billingAddress = billingAddress;
	}

	public List<Transaction> getSentTransactions() {
		return sentTransactions;
	}

	public void setSentTransactions(List<Transaction> sentTransactions) {
		this.sentTransactions = sentTransactions;
	}

	public List<Transaction> getReceivedTransactions() {
		return receivedTransactions;
	}

	public void setReceivedTransactions(List<Transaction> receivedTransactions) {
		this.receivedTransactions = receivedTransactions;
	}

	public List<String> getContactList() {
		return contactList;
	}

	public void setContactList(List<String> contactList) {
		this.contactList = contactList;
	}

	public String getBankAccountNumber() {
		return bankAccountNumber;
	}

	public void setBankAccountNumber(String bankAccountNumber) {
		this.bankAccountNumber = bankAccountNumber;
	}
}
