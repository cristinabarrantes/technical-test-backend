package com.playtomic.tests.wallet.api;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playtomic.tests.wallet.dto.TopUpRequest;
import com.playtomic.tests.wallet.entity.Wallet;
import com.playtomic.tests.wallet.service.StripeAmountTooSmallException;
import com.playtomic.tests.wallet.service.WalletService;

@WebMvcTest(WalletController.class)
public class WalletControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private WalletService walletService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void test_log_ok() throws Exception {
		mockMvc.perform(get("/"))
			.andExpect(status().isOk());
		mockMvc.perform(post("/"))
			.andExpect(status().isOk());
		mockMvc.perform(put("/"))
			.andExpect(status().isOk());
	}

	@Test
	void test_wallet_ok() throws Exception {
		Wallet wallet = new Wallet(new BigDecimal(10));
		Integer walletId = 1;
		wallet.setId(walletId);
		when(walletService.getWallet(walletId)).thenReturn(wallet);
    	
		mockMvc.perform(get("/wallet/" + walletId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.balance").value(wallet.getBalance()));
	}

	@Test
	void test_wallet_notFound() throws Exception {
		Integer walletId = 111;
		when(walletService.getWallet(walletId)).thenThrow(new NoSuchElementException());
		mockMvc.perform(get("/wallet/" + walletId))
			.andExpect(status().isNotFound());
	}

	@Test
	void test_topUpMoney_ok() throws Exception {
		TopUpRequest request = new TopUpRequest("1234 1234 1234 1234", new BigDecimal(50));
		BigDecimal finalAmount = new BigDecimal(50);
		Integer walletId = 1;
		when(walletService.topUpMoney(walletId, request.getCreditCardNumber(), request.getAmount()))
			.thenReturn(finalAmount);
		mockMvc.perform(post("/wallet/" + walletId + "/top-up")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.balance").value(finalAmount));
	}

	@Test
	void test_topUpMoney_noBody_exception() throws Exception {
		mockMvc.perform(post("/wallet/1/top-up"))
			.andExpect(status().isBadRequest());
	}

	/************************/
	/* Errors in validators */
	/************************/
	@Test
	void test_topUpMoney_negativeBalance_exception() throws Exception {
		TopUpRequest request = new TopUpRequest("1234 1234 1234 1234", new BigDecimal(-50));
		mockMvc.perform(post("/wallet/1/top-up")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("Value for amount must be greater than 0"));
	}

	@Test
	void test_topUpMoney_chargeAmountTooBig_exception() throws Exception {
		TopUpRequest request = new TopUpRequest("1234 1234 1234 1234", new BigDecimal(1000000));
		mockMvc.perform(post("/wallet/1/top-up")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("Value for amount must be less than or equal to 999999.99"));
	}
	@Test
	void test_topUpMoney_chargeMoreDecimalsThanAllowed_exception() throws Exception {
		TopUpRequest request = new TopUpRequest("1234 1234 1234 1234", new BigDecimal("12.3456"));
		mockMvc.perform(post("/wallet/1/top-up")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("Value for amount must have at maximum 2 decimals"));
	}

	/***********************************/
	/* Simulates exceptions in service */
	/***********************************/
	@Test
	void test_topUpMoney_chargeAmountTooSmall_exception() throws Exception {
		TopUpRequest request = new TopUpRequest("1234 1234 1234 1234", new BigDecimal(1));
		when(walletService.topUpMoney(anyInt(), anyString(), argThat(
				amount -> amount.compareTo(new BigDecimal(10)) < 0 && amount.compareTo(BigDecimal.ZERO) > 0)))
			.thenThrow(new StripeAmountTooSmallException());
		mockMvc.perform(post("/wallet/1/top-up")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("Amount too small"));
	}

	@Test
	void test_topUpMoney_chargeSumTooBig_exception() throws Exception {
		TopUpRequest request = new TopUpRequest("1234 1234 1234 1234", new BigDecimal("999999.99"));
		String msg = "It is not possible to increase the balance of the wallet";
		// The value is allowed because it is the maximum but the test assumes than the balance is > 0
		// so the sum is greater than the value allowed
		when(walletService.topUpMoney(anyInt(), anyString(), argThat(
				amount -> amount.compareTo(new BigDecimal("999999.99")) == 0)))
			.thenThrow(new IllegalStateException(msg));
		mockMvc.perform(post("/wallet/1/top-up")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().is4xxClientError())
			.andExpect(jsonPath("$.message").value(msg));
	}
}
