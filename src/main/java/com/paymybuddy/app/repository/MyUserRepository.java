package com.paymybuddy.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paymybuddy.app.model.MyUser;

@Repository
public interface MyUserRepository extends JpaRepository<MyUser, Long> {

	public Optional<MyUser> findByEmail(String email);
}
