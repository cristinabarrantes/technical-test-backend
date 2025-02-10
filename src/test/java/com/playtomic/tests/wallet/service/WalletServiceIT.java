package com.playtomic.tests.wallet.service;

import java.math.BigDecimal;
import java.util.ConcurrentModificationException;
import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.playtomic.tests.wallet.entity.Wallet;
import com.playtomic.tests.wallet.repository.WalletRepository;

@SpringBootTest
@ActiveProfiles(profiles = "test")
public class WalletServiceIT {

	@Autowired
	private WalletRepository walletRepository;

	@Autowired
	private WalletService walletService;

    @MockBean
    private StripeService stripeService;
   
    @Test
    void test_topUpMoney_ok() {
    	BigDecimal balance = new BigDecimal("50");    	
        Wallet wallet = walletRepository.save(new Wallet(balance));
        Integer walletId = wallet.getId();
        BigDecimal finalAmount = walletService.chargeAmount(walletId, balance);
        Assertions.assertEquals(0, new BigDecimal("100").compareTo(finalAmount));
    }

	@Test
	void test_topUpMoney_chargeSumTooBig_exception() {
    	BigDecimal balance = new BigDecimal("50");    	
        Wallet wallet = walletRepository.save(new Wallet(balance));
        Integer walletId = wallet.getId();
        when(stripeService.charge(anyString(), any(BigDecimal.class)))
        	.thenReturn(new Payment("12345"));
        Assertions.assertThrows(IllegalStateException.class,
			() -> walletService.topUpMoney(walletId, "1234 1234 1234 1234", new BigDecimal("999999")));
	}

    @Test
    void test_topUpMoney_chargeInStripe_exception() {
    	BigDecimal balance = new BigDecimal("50");
    	Wallet wallet = walletRepository.save(new Wallet(balance));

    	when(stripeService.charge(anyString(), any(BigDecimal.class)))
    		.thenThrow(new StripeServiceException());

    	Assertions.assertThrows(StripeServiceException.class,
			() -> walletService.topUpMoney(wallet.getId(), "1234 1234 1234 1234", new BigDecimal("10")));
    }

    @Test
    void test_chargeAmount_ok() throws InterruptedException {
    	BigDecimal balance = new BigDecimal("50");    	
        Wallet wallet = walletRepository.save(new Wallet(balance));
        Integer walletId = wallet.getId();
        Integer version = wallet.getVersion();

        BigDecimal incOne = new BigDecimal("100");
    	BigDecimal incTwo = new BigDecimal("200");
    	
    	chargeAmount(walletId, balance, incOne, incTwo, 2);

    	wallet = walletRepository.findById(walletId).get();
        Assertions.assertEquals(0, new BigDecimal("350").compareTo(wallet.getBalance()));
        Assertions.assertEquals(version + 2, wallet.getVersion());
        walletRepository.deleteById(walletId);
    }

    @Test
    void test_chargeAmount_concurrentUpdates_exception() throws InterruptedException {
    	BigDecimal balance = new BigDecimal("50");
        Wallet wallet = walletRepository.save(new Wallet(balance));
        Integer walletId = wallet.getId();
        Integer version = wallet.getVersion();

        BigDecimal incOne = new BigDecimal("100");
    	BigDecimal incTwo = new BigDecimal("200");
    	chargeAmount(walletId, balance, incOne, incTwo, 1);

    	wallet = walletRepository.findById(walletId).get();
        Assertions.assertEquals(-1, wallet.getBalance().compareTo(new BigDecimal("350")));
        Assertions.assertEquals(version + 1, wallet.getVersion());
        walletRepository.deleteById(walletId);
    }

    private void chargeAmount(Integer walletId, BigDecimal balance, BigDecimal incOne, BigDecimal incTwo, int maxAttempts) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(2);

    	Thread thread1 = new Thread(() -> {
            try {
        		latch.await();
                walletService.chargeAmount(walletId, incOne, maxAttempts);
            } catch (ConcurrentModificationException e) {
                System.out.println("Thread 1 - Optimistic lock exception.");
            } catch (InterruptedException e) {
            	System.out.println("Problems with thread 1: " + e.getMessage());
			}
        });

        Thread thread2 = new Thread(() -> {
            try {
            	latch.await();
                walletService.chargeAmount(walletId, incTwo, maxAttempts);
            } catch (ConcurrentModificationException e) {
                System.out.println("Thread 2 - Optimistic lock exception.");
            } catch (InterruptedException e) {
            	System.out.println("Problems with thread 2: " + e.getMessage());
            }
        });

        thread1.start();
        thread2.start();

        latch.countDown();
        latch.countDown();

        thread1.join();
        thread2.join();
    }
}
