package com.playtomic.tests.wallet.service;

import org.springframework.stereotype.Service;

import com.playtomic.tests.wallet.entity.Wallet;
import com.playtomic.tests.wallet.repository.WalletRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WalletService {

	private final WalletRepository walletRepository;

	public WalletService(WalletRepository walletRepository) {
		this.walletRepository = walletRepository;
	}

	public Wallet getWallet(int id) {
		return walletRepository.findById(id).orElseThrow(
			() -> new RuntimeException(("Wallet not found")));
	}
}
