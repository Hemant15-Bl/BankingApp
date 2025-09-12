package com.java.main.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.java.main.entities.Account;

public interface AccountRepository extends JpaRepository<Account, Long>{
	
	Optional<Account> findByUsername(String username);
}
