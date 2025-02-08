package com.playtomic.tests.wallet.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BalanceResponse {

	@NonNull
	private Integer walletId;

	@NonNull
	private BigDecimal balance;

}
