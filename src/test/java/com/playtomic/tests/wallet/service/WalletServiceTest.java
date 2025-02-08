package com.playtomic.tests.wallet.service;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.ArgumentMatchers.anyString;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import org.mockito.junit.jupiter.MockitoExtension;

import com.playtomic.tests.wallet.entity.Wallet;
import com.playtomic.tests.wallet.repository.WalletRepository;

@ExtendWith(MockitoExtension.class)
public class WalletServiceTest {

	@Mock
	private WalletRepository walletRepository;

	@Mock
	private StripeService stripeService;

	@InjectMocks
	private WalletService walletService;

	@Test
	void test_getWallet_ok() {
		BigDecimal balance = new BigDecimal("20");
		Wallet wallet = new Wallet(balance);
		Integer walletId = 1;
		doReturn(Optional.of(wallet)).when(walletRepository).findById(walletId);

		Wallet result = walletService.getWallet(walletId);
		Assertions.assertEquals(0, balance.compareTo(result.getBalance()));
	}

	@Test
	void test_getWallet_NoSuchElement_exception() {
		Assertions.assertThrows(NoSuchElementException.class, () -> walletService.getWallet(111));
	}

	@Test
	void test_getWallet_NullPointer_exception() {
		Assertions.assertThrows(NullPointerException.class, () -> walletService.getWallet(null));
	}

	@Test
	void test_topUpMoney_ok() {
		BigDecimal balance = new BigDecimal("20");
		Wallet wallet = new Wallet(balance);
		Integer walletId = 1;
		doReturn(Optional.of(wallet)).when(walletRepository).findById(walletId);

		BigDecimal amount = new BigDecimal("50");
		BigDecimal expected = balance.add(amount);
		doReturn(expected).when(walletRepository).getBalanceById(walletId);

		BigDecimal finalBalance = walletService.topUpMoney(walletId, "1234 1234 1234 1234", amount);
		Assertions.assertEquals(0, expected.compareTo(finalBalance));
	}

	@Test
	void test_topUpMoney_noWalletForId_exception() {
		Assertions.assertThrows(NoSuchElementException.class,
			() -> walletService.topUpMoney(111, "1234 1234 1234 1234", new BigDecimal("10")));
	}

	@Test
	void test_topUpMoney_nullValue_exception() {
		Assertions.assertThrows(NullPointerException.class,
			() -> walletService.topUpMoney(null, "1234 1234 1234 1234", new BigDecimal("10")));
		Assertions.assertThrows(NullPointerException.class,
			() -> walletService.topUpMoney(111, null, new BigDecimal("10")));
		Assertions.assertThrows(NullPointerException.class,
			() -> walletService.topUpMoney(111, "1234 1234 1234 1234", null));
	}

	@Test
	void test_topUpMoney_negativeBalance_exception() {
		Assertions.assertThrows(IllegalArgumentException.class,
			() -> walletService.topUpMoney(1, "1234 1234 1234 1234", new BigDecimal("-10")));
	}

	@Test
	void test_refund_ok() {
		boolean success = walletService.refund(1, "12345", WalletService.MAX_REFUND_ATTEMPTS);
		Assertions.assertTrue(success);
	}

	@Test
	void test_refund_exception() {
		doThrow(new StripeServiceException()).when(stripeService).refund(anyString());
		Assertions.assertThrows(IllegalStateException.class,
			() -> walletService.refund(1, "12345", 3));
	}
}
