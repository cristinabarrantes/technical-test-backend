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
	  void test() {
	  	  Assertions.assertNotNull(walletRepository);
	  	  Wallet w1 = new Wallet(1, new BigDecimal("20"));
	  	  walletRepository.save(w1);
	  	  Optional<Wallet> ow2 = walletRepository.findById(1);
	  	  Assertions.assertTrue(ow2.isPresent());
	  	  Wallet w2 = ow2.get();
	  	  System.out.println(w2);
	  	  Assertions.assertEquals(w1,  w2);
	  }

}
