package com.java.main.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Transaction {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private BigDecimal amount;
	private String type;
	private LocalDateTime timpstamp;
	
	@ManyToOne
	@JoinColumn(name = "account")
	private Account account;

	public Transaction(BigDecimal amount, String type, LocalDateTime timpstamp, Account account) {
		this.amount = amount;
		this.type = type;
		this.timpstamp = timpstamp;
		this.account = account;
	}
	
}
