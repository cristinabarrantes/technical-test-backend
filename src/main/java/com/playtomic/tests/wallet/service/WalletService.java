package com.playtomic.tests.wallet.service;

import java.math.BigDecimal;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Supplier;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.playtomic.tests.wallet.entity.Wallet;
import com.playtomic.tests.wallet.repository.WalletRepository;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WalletService {

	public static final int MAX_LOCK_ATTEMPTS = 3;

	public static final int MAX_REFUND_ATTEMPTS = 10;

	private final WalletRepository walletRepository;

	private final StripeService stripeService;

	public WalletService(WalletRepository walletRepository, StripeService stripeService) {
		this.walletRepository = walletRepository;
		this.stripeService = stripeService;
	}

	@Transactional(readOnly = true)
	public Wallet getWallet(@NonNull Integer walletId) {
		return walletRepository.findById(walletId).orElseThrow(walletNotFound());
	}

	@Transactional
	public BigDecimal topUpMoney(@NonNull Integer walletId, @NonNull String creditCardNumber, @NonNull BigDecimal amount) {
		checkAndThrow(walletId, amount);
		log.info("Charging {} to the wallet with id {} using a credit card", amount, walletId);
		Payment payment;
		try {
			payment = stripeService.charge(creditCardNumber, amount);
			log.info("Amount charged using stripe for wallet with id {}", walletId);
		} catch (StripeServiceException e) {
			log.error("Problems executing a charge in Stripe");
			throw e;
		}
		BigDecimal newBalance;
		try {
			newBalance = chargeAmount(walletId, amount);
		} catch (Exception e) {
			log.error("Problems adding an amount to the wallet with id " + walletId + ": " + e.getMessage());
			refund(walletId, payment.getId(), MAX_REFUND_ATTEMPTS);
			throw new IllegalStateException("It is not possible to increase the balance of the wallet");
		}
		return newBalance;
	}

	private void checkAndThrow(Integer walletId, BigDecimal amount) {
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			log.error("It is not possible to charge the non positive amount {} to the wallet id {}", amount, walletId);
			throw new IllegalArgumentException("A non positive amount cannot be loaded in a wallet");
		}
		if (walletRepository.findById(walletId).isEmpty()) {
			log.error("It is not possible to charge to an inexistent wallet with id {}", walletId);
			throw exceptionWalletNotFound();
		}
	}

	protected boolean refund(Integer walletId, String paymentId, int maxAttempts) {
		boolean success = false;
		int attempts = 1;
		int baseDelay = 10;
		do {
			try {
				stripeService.refund(paymentId);
				success = true;
				log.info("Amount refunded for wallet with id {} due to the previous error", walletId);
			} catch (StripeServiceException e) {
				log.error("Problems in the attempt {} to refund payment with id {}", attempts, paymentId);
				int delay = baseDelay * (1 << (attempts - 1));
				attempts++;
				LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(delay));
			}
		} while (!success && attempts <= maxAttempts);
		if (!success) {
			log.error("CRITICAL: it has not been possible to refund the amount of the payment with id {} for wallet with id {} after {} attempts",
				paymentId, walletId, attempts-1);
			throw new IllegalStateException("It has not been possible to refund the amount after a failed top up money operation");
		}
		return success;
	}

	protected BigDecimal chargeAmount(Integer walletId, BigDecimal balance) {
		return chargeAmount(walletId, balance, MAX_LOCK_ATTEMPTS);
	}

	protected BigDecimal chargeAmount(Integer walletId, BigDecimal balance, int maxAttempts) {
		boolean success = false;
		int attempts = 0;
		Wallet wallet;
		do {
			wallet = walletRepository.findById(walletId).orElseThrow(walletNotFound());
			wallet.setBalance(wallet.getBalance().add(balance));
			try {
				walletRepository.save(wallet);
				success = true;
			} catch (ObjectOptimisticLockingFailureException e) {
				attempts++;
			}
		} while (!success && attempts < maxAttempts);
		if (!success) {
			log.error("Balance has not been updated for wallet with id {} due to concurrent updates for same wallet", walletId);
			throw new ConcurrentModificationException("Balance has not been updated due to concurrent updates");
		}
		return walletRepository.getBalanceById(walletId);
	}

	private Supplier<NoSuchElementException> walletNotFound() {
		return () -> exceptionWalletNotFound();
	}

	private NoSuchElementException exceptionWalletNotFound() {
		return new NoSuchElementException("Wallet not found");
	}
}
