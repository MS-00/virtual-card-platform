package com.mohit_sunda.virtual_card_platform.repository;

import com.mohit_sunda.virtual_card_platform.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CardRepository extends JpaRepository<Card, UUID> {
}
