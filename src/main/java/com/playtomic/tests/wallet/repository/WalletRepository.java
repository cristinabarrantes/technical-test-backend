package com.playtomic.tests.wallet.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.playtomic.tests.wallet.entity.Wallet;

@Repository
public interface WalletRepository extends CrudRepository<Wallet, Integer> {

	@Query("SELECT w.balance FROM Wallet w WHERE w.id = :id")
	BigDecimal getBalanceById(@Param("id") Integer id);
}
