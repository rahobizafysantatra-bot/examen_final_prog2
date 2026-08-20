package com.example.accounttransaction.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    private String id;
    private AccountType accountType;
    private List<Transaction> transactions;
}
