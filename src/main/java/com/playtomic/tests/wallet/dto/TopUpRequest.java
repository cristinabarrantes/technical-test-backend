package com.playtomic.tests.wallet.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class TopUpRequest {

	@NotNull
	private Integer walletId;

	@NotNull
	private String creditCardNumber;

	@NotNull
	@Positive
	@Max(999999)
	private BigDecimal amount;

}
