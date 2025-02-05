package com.playtomic.tests.wallet.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import org.mockito.junit.jupiter.MockitoExtension;

import com.playtomic.tests.wallet.entity.Wallet;
import com.playtomic.tests.wallet.repository.WalletRepository;
import com.playtomic.tests.wallet.util.TestData;

@ExtendWith(MockitoExtension.class)
public class WalletServiceTest {

	@Mock
	private WalletRepository walletRepository;

	@InjectMocks
	private WalletService walletService;

	@Test
	void test_ok() {
		Wallet wallet = TestData.WALLET;
		Integer id = wallet.getId();
		BigDecimal balance = wallet.getBalance();
		doReturn(Optional.of(wallet)).when(walletRepository).findById(id);
		Wallet result = walletService.getWallet(id);
		Assertions.assertEquals(balance, result.getBalance());
	}

	@Test
	void test_exception() {
		Assertions.assertThrows(RuntimeException.class, () -> walletService.getWallet(111));
	}
}
