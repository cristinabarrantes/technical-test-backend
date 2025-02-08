package com.playtomic.tests.wallet.api;

import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.playtomic.tests.wallet.dto.BalanceResponse;
import com.playtomic.tests.wallet.dto.TopUpRequest;
import com.playtomic.tests.wallet.entity.Wallet;
import com.playtomic.tests.wallet.service.WalletService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class WalletController {

	private final WalletService walletService;

	public WalletController(WalletService walletService) {
		this.walletService = walletService;
	}

	@RequestMapping("/")
	void log() {
		log.info("Logging from /");
	}

	@GetMapping("/wallet/{walletId:\\d+}")
	public ResponseEntity<BalanceResponse> getWallet(@PathVariable Integer walletId) {
		log.info("Getting wallet for id {}", walletId);
		Wallet wallet = walletService.getWallet(walletId);
		return ResponseEntity.ok(new BalanceResponse(wallet.getId(), wallet.getBalance()));
    }

	@PostMapping("/wallet/top-up")
	public ResponseEntity<BalanceResponse> topUpMoney(@RequestBody @Valid TopUpRequest topUpRequest) {
		Integer walletId = topUpRequest.getWalletId();
		BigDecimal amount = topUpRequest.getAmount();
		log.info("Topping up money {} for wallet with id {}", amount, walletId);
		BigDecimal finalAmount = walletService.topUpMoney(walletId, topUpRequest.getCreditCardNumber(), amount);
		return ResponseEntity.ok(new BalanceResponse(walletId, finalAmount));
	}
}
