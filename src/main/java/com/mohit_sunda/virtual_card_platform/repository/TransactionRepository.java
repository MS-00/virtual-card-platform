package com.mohit_sunda.virtual_card_platform.repository;

import com.mohit_sunda.virtual_card_platform.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
}
