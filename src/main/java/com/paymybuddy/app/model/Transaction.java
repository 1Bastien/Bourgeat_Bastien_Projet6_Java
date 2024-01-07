package com.paymybuddy.app.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@NotNull
	@Column(nullable = false)
	private long id;

	@NotNull
	@ManyToOne
	@JoinColumn(nullable = false, name = "sender_id")
	private MyUser sender;

	@NotNull
	@ManyToOne
	@JoinColumn(nullable = false, name = "receiver_id")
	private MyUser receiver;

	@NotNull
	@Digits(integer = 10, fraction = 2)
	@Positive(message = "Le montant doit être positif")
	@Column(nullable = false)
	private BigDecimal amount;

	@NotNull
	@Column(nullable = false)
	@FutureOrPresent(message = "La date de transaction doit être présente ou future")
	private LocalDateTime transactionDate;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public MyUser getSender() {
		return sender;
	}

	public void setSender(MyUser sender) {
		this.sender = sender;
	}

	public MyUser getReceiver() {
		return receiver;
	}

	public void setReceiver(MyUser receiver) {
		this.receiver = receiver;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public LocalDateTime getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(LocalDateTime transactionDate) {
		this.transactionDate = transactionDate;
	}
}
