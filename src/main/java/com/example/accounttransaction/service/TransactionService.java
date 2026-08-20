package com.example.accounttransaction.service;

import com.example.accounttransaction.dto.TransactionCreateDto;
import com.example.accounttransaction.exception.InvalidRequestException;
import com.example.accounttransaction.exception.ResourceNotFoundException;
import com.example.accounttransaction.model.Transaction;
import com.example.accounttransaction.model.TransactionType;
import com.example.accounttransaction.repository.AccountRepository;
import com.example.accounttransaction.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionService(
            TransactionRepository transactionRepository,
            AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    public List<Transaction> findByType(String rawType) {
        TransactionType transactionType;

        try {
            transactionType = TransactionType.fromValue(rawType);
        } catch (IllegalArgumentException exception) {
            throw new InvalidRequestException(exception.getMessage());
        }

        return transactionRepository.findAll().stream()
                .filter(transaction -> transaction.getTransactionType() == transactionType)
                .toList();
    }

    public Transaction findById(String id) {
        if (id == null || id.isBlank()) {
            throw new InvalidRequestException("Transaction id is required.");
        }

        Transaction transaction = transactionRepository.findById(id);
        if (transaction == null) {
            throw new ResourceNotFoundException("Transaction not found with id: " + id);
        }

        return transaction;
    }

    public Transaction create(TransactionCreateDto dto) {
        validateCreateDto(dto);

        String accountId = dto.getAccountId().trim();
        if (!accountRepository.existsById(accountId)) {
            throw new ResourceNotFoundException("Account not found with id: " + accountId);
        }

        Transaction transaction = new Transaction(
                generateStringId(),
                Instant.now(),
                dto.getTransactionType(),
                dto.getAmount(),
                dto.getReason().trim(),
                accountId
        );

        Transaction savedTransaction = transactionRepository.save(transaction);
        if (savedTransaction == null) {
            throw new IllegalStateException("Transaction could not be saved.");
        }

        return savedTransaction;
    }

    public Transaction update(Transaction transaction) {
        validateTransaction(transaction);
        findById(transaction.getId());

        if (!accountRepository.existsById(transaction.getAccountId())) {
            throw new ResourceNotFoundException(
                    "Account not found with id: " + transaction.getAccountId());
        }

        Transaction updatedTransaction = transactionRepository.update(transaction);
        if (updatedTransaction == null) {
            throw new IllegalStateException("Transaction could not be updated.");
        }

        return updatedTransaction;
    }

    public boolean deleteById(String id) {
        findById(id);
        return transactionRepository.deleteById(id);
    }

    private String generateStringId() {
        long randomPart = ThreadLocalRandom.current().nextLong();
        return "transaction-"
                + Instant.now().toEpochMilli()
                + "-"
                + Long.toUnsignedString(randomPart, 36);
    }

    private void validateCreateDto(TransactionCreateDto dto) {
        if (dto == null) {
            throw new InvalidRequestException("Transaction body is required.");
        }
        validateCommonFields(
                dto.getAccountId(),
                dto.getTransactionType(),
                dto.getAmount(),
                dto.getReason()
        );
    }

    private void validateTransaction(Transaction transaction) {
        if (transaction == null) {
            throw new InvalidRequestException("Transaction body is required.");
        }
        if (transaction.getId() == null || transaction.getId().isBlank()) {
            throw new InvalidRequestException("Transaction id is required.");
        }
        if (transaction.getCreatedAt() == null) {
            throw new InvalidRequestException("Transaction creation date is required.");
        }
        validateCommonFields(
                transaction.getAccountId(),
                transaction.getTransactionType(),
                transaction.getAmount(),
                transaction.getReason()
        );
    }

    private void validateCommonFields(
            String accountId,
            TransactionType transactionType,
            BigDecimal amount,
            String reason) {
        if (accountId == null || accountId.isBlank()) {
            throw new InvalidRequestException("Account id is required.");
        }
        if (transactionType == null) {
            throw new InvalidRequestException("Transaction type is required.");
        }
        if (amount == null || amount.signum() <= 0) {
            throw new InvalidRequestException("Transaction amount must be greater than zero.");
        }
        if (reason == null || reason.isBlank()) {
            throw new InvalidRequestException("Transaction reason is required.");
        }
    }
}
