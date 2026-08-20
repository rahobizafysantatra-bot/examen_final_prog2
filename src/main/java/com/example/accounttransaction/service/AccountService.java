package com.example.accounttransaction.service;

import com.example.accounttransaction.exception.InvalidRequestException;
import com.example.accounttransaction.exception.ResourceNotFoundException;
import com.example.accounttransaction.model.Account;
import com.example.accounttransaction.model.Transaction;
import com.example.accounttransaction.model.TransactionType;
import com.example.accounttransaction.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    public Account findById(String id) {
        validateId(id);
        Account account = accountRepository.findById(id);

        if (account == null) {
            throw new ResourceNotFoundException("Account not found with id: " + id);
        }

        return account;
    }

    public List<Transaction> findTransactions(String accountId) {
        return findById(accountId).getTransactions();
    }

    public BigDecimal calculateBalance(String accountId) {
        return findTransactions(accountId).stream()
                .map(this::toSignedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Account create(Account account) {
        validateAccount(account);

        if (accountRepository.existsById(account.getId())) {
            throw new InvalidRequestException(
                    "An account already exists with id: " + account.getId());
        }

        Account savedAccount = accountRepository.save(new Account(
                account.getId().trim(),
                account.getAccountType(),
                List.of()
        ));

        if (savedAccount == null) {
            throw new IllegalStateException("Account could not be saved.");
        }

        return savedAccount;
    }

    public Account update(Account account) {
        validateAccount(account);
        findById(account.getId());

        Account updatedAccount = accountRepository.update(account);
        if (updatedAccount == null) {
            throw new IllegalStateException("Account could not be updated.");
        }

        return updatedAccount;
    }

    public boolean deleteById(String id) {
        findById(id);
        return accountRepository.deleteById(id);
    }

    private BigDecimal toSignedAmount(Transaction transaction) {
        return transaction.getTransactionType() == TransactionType.IN
                ? transaction.getAmount()
                : transaction.getAmount().negate();
    }

    private void validateAccount(Account account) {
        if (account == null) {
            throw new InvalidRequestException("Account body is required.");
        }
        validateId(account.getId());
        if (account.getAccountType() == null) {
            throw new InvalidRequestException("Account type is required.");
        }
    }

    private void validateId(String id) {
        if (id == null || id.isBlank()) {
            throw new InvalidRequestException("Account id is required.");
        }
    }
}
