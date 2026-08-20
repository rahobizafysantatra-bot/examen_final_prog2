package com.example.accounttransaction.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

public enum TransactionType {
    IN,
    OUT;

    @JsonCreator
    public static TransactionType fromValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Transaction type must be 'in' or 'out'.");
        }

        try {
            return TransactionType.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Transaction type must be 'in' or 'out'.");
        }
    }

    @JsonValue
    public String toValue() {
        return name().toLowerCase(Locale.ROOT);
    }
}
