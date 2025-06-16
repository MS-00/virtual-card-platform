package com.mohit_sunda.virtual_card_platform.mapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import com.mohit_sunda.virtual_card_platform.dto.CardResponse;
import com.mohit_sunda.virtual_card_platform.dto.TransactionResponse;
import com.mohit_sunda.virtual_card_platform.model.Card;
import com.mohit_sunda.virtual_card_platform.model.Transaction;

public class CardMapper {

    public static CardResponse toCardResponse(Card card) {
        CardResponse resp = new CardResponse();
        resp.setId(card.getId());
        resp.setCardholderName(card.getCardholderName());
        resp.setBalance(CardMapper.setAmount(card.getBalance()));
        resp.setStatus(card.getStatus());
        resp.setCreatedAt(card.getCreatedAt());
        return resp;
    }

    public static TransactionResponse toTransactionResponse(Transaction tx) {
        TransactionResponse resp = new TransactionResponse();
        resp.setId(tx.getId());
        resp.setType(tx.getType().name());
        resp.setAmount(CardMapper.setAmount(tx.getAmount()));
        resp.setCreatedAt(tx.getCreatedAt());
        return resp;
    }

    public static BigDecimal setAmount(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP);
    }
}
