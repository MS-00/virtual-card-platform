package com.mohit_sunda.virtual_card_platform.controller;

import com.mohit_sunda.virtual_card_platform.dto.*;
import com.mohit_sunda.virtual_card_platform.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
@Slf4j
public class CardController {
    private final CardService cardService;

    @PostMapping
    public ResponseEntity<CardResponse> createCard(@Valid @RequestBody CreateCardRequest request) {
        log.info("Creating card for cardholder: {}", request.getCardholderName());

        CardResponse response = cardService.createCard(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CardResponse>> listCards() {
        log.info("Fetching cards");

        List<CardResponse> cards = cardService.getAllCards();
        return ResponseEntity.ok(cards);
    }

    @PostMapping("/{id}/spend")
    public ResponseEntity<ResponseWithMessage> spend(@PathVariable UUID id, @Valid @RequestBody AmountRequest request) {
        log.info("Spend on card ID: {} - amount {}", id, request.getAmount());

        cardService.spend(id, request);

        ResponseWithMessage response = new ResponseWithMessage();
        response.setMessage("Spend successful");

        return ResponseEntity.ok(response);

    }

    @PostMapping("/{id}/topup")
    public ResponseEntity<ResponseWithMessage> topup(@PathVariable UUID id, @Valid @RequestBody AmountRequest request) {
        log.info("topup card ID: {} - amount {}", id, request.getAmount());

        cardService.topup(id, request);

        ResponseWithMessage response = new ResponseWithMessage();
        response.setMessage("Topup successful");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardResponse> getCard(@PathVariable UUID id) {
        log.info("Fetching card ID: {}", id);

        CardResponse response = cardService.getCard(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<TransactionResponse>> getTransactions(@PathVariable UUID id) {
        log.info("Fetching transactions for card ID: {}", id);

        List<TransactionResponse> response = cardService.getTransactions(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/block")
    public ResponseEntity<ResponseWithMessage> blockCard(@PathVariable UUID id) {
        log.info("Block card ID: {}", id);

        cardService.blockCard(id);

        ResponseWithMessage response = new ResponseWithMessage();
        response.setMessage("Card blocked successfully");

        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/{id}/unblock")
    public ResponseEntity<ResponseWithMessage> unblockCard(@PathVariable UUID id) {
        log.info("Active card ID: {}", id);

        cardService.unblockCard(id);

        ResponseWithMessage response = new ResponseWithMessage();
        response.setMessage("Card unblocked successfully");

        return ResponseEntity.ok().body(response);
    }
}
