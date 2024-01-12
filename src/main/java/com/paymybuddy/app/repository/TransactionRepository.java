package com.paymybuddy.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paymybuddy.app.model.MyUser;
import com.paymybuddy.app.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	List<Transaction> findBySender(MyUser sender);

	List<Transaction> findByReceiver(MyUser receiver);
}
