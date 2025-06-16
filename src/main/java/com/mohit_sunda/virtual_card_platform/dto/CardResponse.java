package com.mohit_sunda.virtual_card_platform.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;
import lombok.Data;

@Data
public class CardResponse {

    private UUID id;

    private String cardholderName;

    private BigDecimal balance;

    private Timestamp createdAt;
}
