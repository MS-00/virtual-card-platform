package com.mohit_sunda.virtual_card_platform.mapper;

import com.mohit_sunda.virtual_card_platform.dto.CardResponse;
import com.mohit_sunda.virtual_card_platform.model.Card;

public class CardMapper {

    public static CardResponse toCardResponse(Card card) {
        CardResponse resp = new CardResponse();
        resp.setId(card.getId());
        resp.setCardholderName(card.getCardholderName());
        resp.setBalance(card.getBalance());
        resp.setCreatedAt(card.getCreatedAt());
        return resp;
    }
}
