package com.playtomic.tests.wallet.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.playtomic.tests.wallet.entity.Wallet;

@Repository
public interface WalletRepository extends CrudRepository<Wallet, Integer> {}
