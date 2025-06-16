package com.mohit_sunda.virtual_card_platform.service;

import com.mohit_sunda.virtual_card_platform.dto.*;
import com.mohit_sunda.virtual_card_platform.exception.CardBlockedException;
import com.mohit_sunda.virtual_card_platform.exception.CardNotFoundException;
import com.mohit_sunda.virtual_card_platform.exception.InsufficientBalanceException;
import com.mohit_sunda.virtual_card_platform.exception.RateLimitExceededException;
import com.mohit_sunda.virtual_card_platform.model.*;
import com.mohit_sunda.virtual_card_platform.repository.*;
import com.mohit_sunda.virtual_card_platform.mapper.CardMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;

    private static final ConcurrentHashMap<UUID, Object> cardLocks = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<UUID, List<Long>> spendTimestamps = new ConcurrentHashMap<>();

    @Value("${card.spend.max-per-minute}")
    private int maxSpendsPerMinute;

    @Transactional
    public CardResponse createCard(CreateCardRequest request) {
        Card card = new Card();

        card.setCardholderName(request.getCardholderName());
        card.setBalance(request.getInitialBalance());
        card.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        card = cardRepository.save(card);

        Transaction tx = new Transaction();

        tx.setCard(card);
        tx.setType(TransactionType.INITIAL);
        tx.setAmount(request.getInitialBalance());
        tx.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        transactionRepository.save(tx);

        return CardMapper.toCardResponse(card);
    }

    private Card getCardOrThrow(UUID cardId) {
        try {
            return cardRepository.findById(cardId).orElseThrow();
        } catch (Exception e) {
            throw new CardNotFoundException("Card not found with ID: " + cardId);
        }
    }

    public CardResponse getCard(UUID cardId) {
        Card card = getCardOrThrow(cardId);
        return CardMapper.toCardResponse(card);
    }

    @Transactional
    public CardResponse spend(UUID cardId, AmountRequest request) {
        Card card = getCardOrThrow(cardId);

        if (card.getStatus() == CardStatus.BLOCKED) {
            throw new CardBlockedException("No spending allowed.");
        }

        this.checkRateLimit(cardId);

        Object lock = cardLocks.computeIfAbsent(card.getId(), k -> new Object());

        synchronized (lock) {
            if (card.getBalance().compareTo(request.getAmount()) < 0) {
                throw new InsufficientBalanceException("Current balance: " + card.getBalance()
                        + ", Amount to spend: " + request.getAmount());
            }

            card.setBalance(card.getBalance().subtract(request.getAmount()));
            cardRepository.save(card);

            Transaction tx = new Transaction();
            tx.setCard(card);
            tx.setType(TransactionType.SPEND);
            tx.setAmount(request.getAmount());
            tx.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            transactionRepository.save(tx);

            return CardMapper.toCardResponse(card);
        }
    }

    public void checkRateLimit(UUID cardId) {
        long now = System.currentTimeMillis();
        spendTimestamps.putIfAbsent(cardId, new ArrayList<>());
        List<Long> timestamps = spendTimestamps.get(cardId);

        // Remove timestamps older than 1 minute
        timestamps.removeIf(ts -> now - ts > 60_000);

        if (timestamps.size() >= maxSpendsPerMinute) {
            throw new RateLimitExceededException(
                    "Max " + maxSpendsPerMinute + " spends per minute exceeded for this card.");
        }

        timestamps.add(now);
    }

    @Transactional
    public CardResponse topup(UUID cardId, AmountRequest request) {
        Card card = getCardOrThrow(cardId);

        if (card.getStatus() == CardStatus.BLOCKED) {
            throw new CardBlockedException("No funding allowed.");
        }

        Object lock = cardLocks.computeIfAbsent(card.getId(), k -> new Object());

        synchronized (lock) {
            card.setBalance(card.getBalance().add(request.getAmount()));
            cardRepository.save(card);

            Transaction tx = new Transaction();
            tx.setCard(card);
            tx.setType(TransactionType.TOPUP);
            tx.setAmount(request.getAmount());
            tx.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            transactionRepository.save(tx);

            return CardMapper.toCardResponse(card);
        }
    }

    public List<TransactionResponse> getTransactions(UUID cardId) {
        Card card = getCardOrThrow(cardId);

        return transactionRepository.findAll().stream()
                .filter(tx -> tx.getCard().getId().equals(card.getId()))
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(CardMapper::toTransactionResponse)
                .toList();
    }

    public List<CardResponse> getAllCards() {
        return cardRepository.findAll()
                .stream()
                .map(CardMapper::toCardResponse)
                .toList();
    }

    public void blockCard(UUID cardId) {
        Card card = getCardOrThrow(cardId);
        card.setStatus(CardStatus.BLOCKED);
        cardRepository.save(card);
    }

    public void unblockCard(UUID cardId) {
        Card card = getCardOrThrow(cardId);
        card.setStatus(CardStatus.ACTIVE);
        cardRepository.save(card);
    }
}
