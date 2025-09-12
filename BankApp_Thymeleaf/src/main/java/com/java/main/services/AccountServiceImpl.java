package com.java.main.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.java.main.entities.Account;
import com.java.main.entities.Transaction;
import com.java.main.repository.AccountRepository;
import com.java.main.repository.TransactionRepository;

@Service
public class AccountServiceImpl implements AccountService, UserDetailsService{
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private TransactionRepository transactionRepository;
	
	@Override
	public Account getByUsername(String username) {
			Account account = accountRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Account Not Found With Username "+username));
			if(account!=null) {
				return account;
			}
		
		return null;
	}

	@Override
	public Account registerAccount(String username, String password) {
			if(accountRepository.findByUsername(username).isPresent()) {
				throw new RuntimeException("Account with that username is already exist!");
			}
			
			Account account = new Account();
			account.setUsername(username);
			account.setPassword(passwordEncoder.encode(password));
			account.setBalance(BigDecimal.ZERO);  // Initial Balance set by 0
			return accountRepository.save(account);
		
	}

	@Override
	public void deposit(Account account, BigDecimal amount) {
		BigDecimal bal = account.getBalance();
		account.setBalance(bal.add(amount));
		accountRepository.save(account);
		
		Transaction transaction = new Transaction(amount,"Deposit",LocalDateTime.now(),account);
		transactionRepository.save(transaction);
	}

	@Override
	public void withdraw(Account account, BigDecimal amount) {
		BigDecimal bal = account.getBalance();
		if(bal.compareTo(amount)<0) {
			throw new RuntimeException("Insufficient Balance");
		}
		account.setBalance(bal.subtract(amount));
		accountRepository.save(account);
		
		Transaction transaction = new Transaction(amount,"Withdrawal",LocalDateTime.now(),account);
		transactionRepository.save(transaction);
	}

	@Override
	public List<Transaction> getTransactionHistory(Account account) {
		return transactionRepository.findByAccountId(account.getId());
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Account account = getByUsername(username);
		if(account==null) {
			throw new UsernameNotFoundException("Username or Password Not Found!");
		}
		return new Account(account.getUsername(), account.getPassword(), account.getBalance(), account.getTransactions(), authorities());
	}

	@Override
	public Collection<? extends GrantedAuthority> authorities() {
		return Arrays.asList(new SimpleGrantedAuthority("USER"));
	}

	@Override
	public void fundTransfer(Account fromAccount, String toUsername, BigDecimal amount) {
		BigDecimal frombal = fromAccount.getBalance();
		if(frombal.compareTo(amount)<0) {
			throw new RuntimeException("Insufficient Balance");
		}
		
		Account toAccount = accountRepository.findByUsername(toUsername).orElseThrow(() -> new RuntimeException("Recipient Not Found"));
		
		// deduct From Account
		fromAccount.setBalance(frombal.subtract(amount));
		accountRepository.save(fromAccount);
		
		//Add ToAccount
		toAccount.setBalance(toAccount.getBalance().add(amount));
		accountRepository.save(toAccount);
		
		//create transaction fromAccount
		Transaction debittransaction = new Transaction(amount,"Fund Transfer out to "+toAccount.getUsername(),LocalDateTime.now(),fromAccount);
		transactionRepository.save(debittransaction);
		
		//debit transaction fromAccount
				Transaction createtransaction = new Transaction(amount,"Fund Transfer in to "+fromAccount.getUsername(),LocalDateTime.now(),toAccount);
				transactionRepository.save(createtransaction);
	}
	

	

}
