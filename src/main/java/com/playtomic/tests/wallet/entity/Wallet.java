package com.playtomic.tests.wallet.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "wallet")
@NoArgsConstructor
@Getter
@Setter
public class Wallet {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer id;

	@Column(precision = 8, scale = 2)
	private BigDecimal balance;

	@Version
	private Integer version;

	public Wallet(BigDecimal balance) {
		this.balance = balance;
	}
}
