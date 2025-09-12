package com.java.main.services;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import com.java.main.entities.Account;
import com.java.main.entities.Transaction;

public interface AccountService {
	public Account getByUsername(String username);
	public Account registerAccount(String username, String password);
	public void deposit(Account account,BigDecimal amount);
	public void withdraw(Account account,BigDecimal amount);
	public List<Transaction> getTransactionHistory(Account account);
	public Collection<? extends GrantedAuthority> authorities();
	public void fundTransfer(Account fromAccount,String toUsername,BigDecimal amount);
}
