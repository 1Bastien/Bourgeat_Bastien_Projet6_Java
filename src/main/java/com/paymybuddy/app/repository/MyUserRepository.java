package com.paymybuddy.app.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.paymybuddy.app.model.MyUser;

@Repository
public interface MyUserRepository extends CrudRepository<MyUser, Long> {

	public Optional<MyUser> findByEmail(String email);
}
