package com.playtomic.tests.wallet.api;

import java.math.BigDecimal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.playtomic.tests.wallet.dto.BalanceResponse;
import com.playtomic.tests.wallet.dto.TopUpRequest;
import com.playtomic.tests.wallet.entity.Wallet;
import com.playtomic.tests.wallet.repository.WalletRepository;
import com.playtomic.tests.wallet.service.StripeService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class WalletControllerIT {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private WalletRepository walletRepository;

	@MockBean
	private StripeService stripeService;

	private static final Wallet WALLET = new Wallet(new BigDecimal("20"));

	@BeforeEach
	void setUp() {
		walletRepository.save(WALLET);
	}

	@Test
	void test_wallet_ok() {
		ResponseEntity<Wallet> response = restTemplate.getForEntity("/wallet/" + WALLET.getId(), Wallet.class);
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertEquals(0, WALLET.getBalance().compareTo(response.getBody().getBalance()));
	}

	@Test
	void test_topUp_ok() {
		BigDecimal amount = new BigDecimal("50");
		TopUpRequest request = new TopUpRequest(WALLET.getId(), "1234 1234 1234 1234", amount);
		ResponseEntity<BalanceResponse> response = restTemplate.postForEntity("/wallet/top-up", request, BalanceResponse.class);
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertEquals(0, WALLET.getBalance().add(amount).compareTo(response.getBody().getBalance()));
	}
}
