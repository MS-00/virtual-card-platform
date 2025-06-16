package com.mohit_sunda.virtual_card_platform.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;

@SpringBootTest
@AutoConfigureMockMvc
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String cardId;

    @Value("${card.spend.max-per-minute}")
    private int maxSpendsPerMinute;

    @BeforeEach
    void setup() throws Exception {
        // Create a card for use in all tests
        String json = "{\"cardholderName\":\"Integration User\",\"initialBalance\":100.00}";
        String response = mockMvc.perform(post("/cards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        cardId = objectMapper.readTree(response).get("id").asText();
    }

    @Test
    void testSpendEndpoint() throws Exception {
        String json = "{\"amount\":50.00}";
        mockMvc.perform(post("/cards/" + cardId + "/spend")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void testTopupEndpoint() throws Exception {
        String json = "{\"amount\":25.00}";
        mockMvc.perform(post("/cards/" + cardId + "/topup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void testGetCardEndpoint() throws Exception {
        mockMvc.perform(get("/cards/" + cardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cardId));
    }

    @Test
    void testGetTransactionsEndpoint() throws Exception {
        mockMvc.perform(get("/cards/" + cardId + "/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testListCardsEndpoint() throws Exception {
        mockMvc.perform(get("/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testRateLimitExceeded() throws Exception {
        // Create a card
        BigDecimal spendAmount = new BigDecimal("1.00");
        BigDecimal initialBalance = spendAmount.multiply(BigDecimal.valueOf(maxSpendsPerMinute + 1));

        String json = String.format("{\"cardholderName\":\"RateLimit User\",\"initialBalance\":%s}", initialBalance);
        String response = mockMvc.perform(post("/cards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String cardId2 = objectMapper.readTree(response).get("id").asText();

        String spendJson = String.format("{\"amount\":%s}", spendAmount);

        // Perform `maxSpendsPerMinute` spends (should succeed)
        for (int i = 0; i < maxSpendsPerMinute; i++) {
            mockMvc.perform(post("/cards/" + cardId2 + "/spend")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(spendJson))
                    .andExpect(status().isOk());
        }
        // The next call should fail
        mockMvc.perform(post("/cards/" + cardId2 + "/spend")
                .contentType(MediaType.APPLICATION_JSON)
                .content(spendJson))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.error").exists());
    }
}
