package com.playtomic.tests.wallet.repository;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.playtomic.tests.wallet.entity.Wallet;
import com.playtomic.tests.wallet.util.TestData;

@DataJpaTest
public class WalletRepositoryIT {

	@Autowired
	WalletRepository walletRepository;

	@Test
	void test_ok() {
		Wallet w1 = TestData.WALLET;
		walletRepository.save(w1);
		Optional<Wallet> ow2 = walletRepository.findById(1);
		Assertions.assertTrue(ow2.isPresent());
		Assertions.assertEquals(0, w1.getBalance().compareTo(ow2.get().getBalance()));
	}
}
