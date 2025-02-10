package com.playtomic.tests.wallet.service;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.math.BigDecimal;
import java.net.URI;

public class StripeServiceIT {

  URI testUri = URI.create("https://sandbox.playtomic.io/v1/stripe-simulator/charges");
  StripeService s = new StripeService(testUri, testUri, new RestTemplateBuilder());

  @Test
  void test_exception() {
      Assertions.assertThrows(StripeAmountTooSmallException.class, () -> {
          s.charge("4242 4242 4242 4242", new BigDecimal(5));
      });
  }

  @Test
  void test_ok() throws StripeServiceException {
      Payment payment = s.charge("4242 4242 4242 4242", new BigDecimal(15));
      Assertions.assertNotNull(payment);
  }
}