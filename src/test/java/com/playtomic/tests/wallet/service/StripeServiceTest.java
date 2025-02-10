package com.playtomic.tests.wallet.service;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

/**
 * This test was failing with the initial implementation because it was trying
 * to access the inexistent URI: "http://how-would-you-test-me.localhost"
 *
 * If the URI configured in develop profile is used, it works. See
 * {@link StripeServiceIT}.
 *
 * But in this case, it has been changed to use a mock configured in such a way
 * that if amount is less than 10, an exception is thrown as it is specified
 * in {@link StripeService} and it happens when calling the URI for dummy
 * Stripe service.
 */
@ExtendWith(MockitoExtension.class)
public class StripeServiceTest {

	@Mock
	StripeService s;

	@Test
	void test_exception() {
		doThrow(new StripeAmountTooSmallException()).when(s).charge(anyString(),
			argThat(amount -> amount.compareTo(new BigDecimal(10)) < 0));
		Assertions.assertThrows(StripeAmountTooSmallException.class, () -> {
			s.charge("4242 4242 4242 4242", new BigDecimal(5));
		});
	}

	@Test
	void test_ok() throws StripeServiceException {
		String paymentId = "12345";
		when(s.charge(anyString(), any(BigDecimal.class)))
			.thenReturn(new Payment(paymentId));
		Payment payment = s.charge("4242 4242 4242 4242", new BigDecimal(15));
		Assertions.assertNotNull(payment);
		Assertions.assertEquals(paymentId, payment.getId());
	}
}
