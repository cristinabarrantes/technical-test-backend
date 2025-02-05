package com.playtomic.tests.wallet.api;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.playtomic.tests.wallet.entity.Wallet;
import com.playtomic.tests.wallet.service.WalletService;
import com.playtomic.tests.wallet.util.TestData;

@WebMvcTest(WalletController.class)
public class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletService walletService;

    @Test
    void test_ok() throws Exception {
    	Wallet wallet = TestData.WALLET;
    	when(walletService.getWallet(wallet.getId())).thenReturn(wallet);
    	
    	mockMvc.perform(get("/wallet/1"))
    		.andExpect(status().isOk())
    		.andExpect(jsonPath("$.balance").value(wallet.getBalance()));
    }

    @Test
    void test_exception() throws Exception {
    	mockMvc.perform(get("/wallet/111"))
			.andExpect(status().isNotFound());
    }
}
