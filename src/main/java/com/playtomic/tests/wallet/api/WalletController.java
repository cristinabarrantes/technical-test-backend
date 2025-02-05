package com.playtomic.tests.wallet.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.playtomic.tests.wallet.entity.Wallet;
import com.playtomic.tests.wallet.service.WalletService;

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

	@GetMapping("/wallet/{id}")
	ResponseEntity<Wallet> wallet(@PathVariable int id) {
	log.info("Getting wallet for id {}", id);
	Wallet wallet = walletService.getWallet(id);
	return (wallet == null)
		? ResponseEntity.notFound().build()
		: ResponseEntity.ok(wallet);
    }
}
