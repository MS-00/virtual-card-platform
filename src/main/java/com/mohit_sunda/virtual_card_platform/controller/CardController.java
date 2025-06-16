package com.mohit_sunda.virtual_card_platform.controller;

import com.mohit_sunda.virtual_card_platform.dto.*;
import com.mohit_sunda.virtual_card_platform.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping("/{id}/spend")
    public void spend(@PathVariable UUID id, @Valid @RequestBody AmountRequest request) {
        // TODO
    }

    @PostMapping("/{id}/topup")
    public void topup(@PathVariable UUID id, @Valid @RequestBody AmountRequest request) {
        // TODO
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardResponse> getCard(@PathVariable UUID id) {
        CardResponse response = cardService.getCard(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/transactions")
    public void getTransactions(@PathVariable UUID id) {
        // TODO
    }
}
