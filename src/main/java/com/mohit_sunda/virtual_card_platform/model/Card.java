package com.mohit_sunda.virtual_card_platform.model;

import lombok.Data;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Data
public class Card {

    @Id
    @GeneratedValue
    private UUID id;

    private String cardholderName;

    private BigDecimal balance;

    private Timestamp createdAt;
}
