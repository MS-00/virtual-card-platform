package com.mohit_sunda.virtual_card_platform.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;
import lombok.Data;

@Data
public class TransactionResponse {

    private UUID id;

    private String type;

    private BigDecimal amount;

    private Timestamp createdAt;
}
