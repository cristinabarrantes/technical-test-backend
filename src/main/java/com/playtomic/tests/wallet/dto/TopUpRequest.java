package com.playtomic.tests.wallet.dto;

import java.math.BigDecimal;

import com.playtomic.tests.wallet.validator.MaxDecimals;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TopUpRequest {

	@NotNull
	private String creditCardNumber;

	@NotNull
	@Positive
	@DecimalMax("999999.99")
    @MaxDecimals(value = 2)
	private BigDecimal amount;

}
