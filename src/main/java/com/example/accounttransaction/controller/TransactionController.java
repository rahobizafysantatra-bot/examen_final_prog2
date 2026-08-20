package com.example.accounttransaction.controller;

import com.example.accounttransaction.dto.TransactionCreateDto;
import com.example.accounttransaction.model.Transaction;
import com.example.accounttransaction.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public List<Transaction> findByType(@RequestParam(value = "type", required = false) String type) {
        if (type != null && !type.isBlank()) {
            return transactionService.findByType(type);
        }
        return transactionService.findAll();
    }


    @PostMapping
    public ResponseEntity<Transaction> create(
            @RequestBody TransactionCreateDto transactionCreateDto) {
        Transaction createdTransaction = transactionService.create(transactionCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTransaction);
    }
}
