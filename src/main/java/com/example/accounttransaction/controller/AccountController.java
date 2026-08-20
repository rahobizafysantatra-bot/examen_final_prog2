package com.example.accounttransaction.controller;

import com.example.accounttransaction.model.Transaction;
import com.example.accounttransaction.service.AccountService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/{id}/transactions")
    public List<Transaction> findTransactions(@PathVariable String id) {
        return accountService.findTransactions(id);
    }

    @GetMapping("/{id}/balance")
    public BigDecimal calculateBalance(@PathVariable String id) {
        return accountService.calculateBalance(id);
    }
}
