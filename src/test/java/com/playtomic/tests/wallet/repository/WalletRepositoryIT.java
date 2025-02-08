package com.playtomic.tests.wallet.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.playtomic.tests.wallet.entity.Wallet;

@DataJpaTest
public class WalletRepositoryIT {

	@Autowired
	WalletRepository walletRepository;

	@Test
	void test_ok() {
		Wallet w = new Wallet(new BigDecimal("100"));
		walletRepository.save(w);
		Optional<Wallet> ow = walletRepository.findById(w.getId());
		Assertions.assertTrue(ow.isPresent());
		Assertions.assertEquals(0, w.getBalance().compareTo(ow.get().getBalance()));
	}
}
