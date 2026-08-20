package com.example.accounttransaction.repository;

import com.example.accounttransaction.config.DatabaseConnectionFactory;
import com.example.accounttransaction.model.Account;
import com.example.accounttransaction.model.AccountType;
import com.example.accounttransaction.model.Transaction;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AccountRepository {

    private final DatabaseConnectionFactory connectionFactory;
    private final TransactionRepository transactionRepository;

    public AccountRepository(
            DatabaseConnectionFactory connectionFactory,
            TransactionRepository transactionRepository
    ) {
        this.connectionFactory = connectionFactory;
        this.transactionRepository = transactionRepository;
    }

    public List<Account> findAll() {
        String sql = """
                SELECT
                    id,
                    account_type
                FROM accounts
                ORDER BY id ASC
                """;

        List<Account> accountsWithoutTransactions = new ArrayList<>();

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                accountsWithoutTransactions.add(
                        mapAccountWithoutTransactions(resultSet)
                );
            }

        } catch (SQLException e) {
            System.out.println("SQL Error : " + e.getMessage());
            return List.of();
        }

        return accountsWithoutTransactions.stream()
                .map(account -> new Account(
                        account.getId(),
                        account.getAccountType(),
                        List.copyOf(
                                transactionRepository.findAllByAccountId(
                                        account.getId()
                                )
                        )
                ))
                .toList();
    }

    public Account findById(String id) {
        String sql = """
                SELECT
                    id,
                    account_type
                FROM accounts
                WHERE id = ?
                """;

        Account accountWithoutTransactions = null;

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    accountWithoutTransactions =
                            mapAccountWithoutTransactions(resultSet);
                }
            }

        } catch (SQLException e) {
            System.out.println("SQL Error : " + e.getMessage());
            return null;
        }

        if (accountWithoutTransactions == null) {
            return null;
        }

        List<Transaction> transactions =
                transactionRepository.findAllByAccountId(
                        accountWithoutTransactions.getId()
                );

        return new Account(
                accountWithoutTransactions.getId(),
                accountWithoutTransactions.getAccountType(),
                List.copyOf(transactions)
        );
    }

    public boolean existsById(String id) {
        String sql = """
                SELECT 1
                FROM accounts
                WHERE id = ?
                """;

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }

        } catch (SQLException e) {
            System.out.println("SQL Error : " + e.getMessage());
        }

        return false;
    }

    public Account save(Account account) {
        String sql = """
                INSERT INTO accounts (
                    id,
                    account_type
                )
                VALUES (
                    ?,
                    CAST(? AS account_type)
                )
                RETURNING
                    id,
                    account_type
                """;

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    account.getId()
            );

            statement.setString(
                    2,
                    account.getAccountType().name()
            );

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Account savedAccount =
                            mapAccountWithoutTransactions(resultSet);

                    return new Account(
                            savedAccount.getId(),
                            savedAccount.getAccountType(),
                            List.of()
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("SQL Error : " + e.getMessage());
        }

        return null;
    }

    public Account update(Account account) {
        String sql = """
                UPDATE accounts
                SET account_type = CAST(? AS account_type)
                WHERE id = ?
                RETURNING
                    id,
                    account_type
                """;

        Account updatedAccount = null;

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    account.getAccountType().name()
            );

            statement.setString(
                    2,
                    account.getId()
            );

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    updatedAccount =
                            mapAccountWithoutTransactions(resultSet);
                }
            }

        } catch (SQLException e) {
            System.out.println("SQL Error : " + e.getMessage());
            return null;
        }

        if (updatedAccount == null) {
            return null;
        }

        List<Transaction> transactions =
                transactionRepository.findAllByAccountId(
                        updatedAccount.getId()
                );

        return new Account(
                updatedAccount.getId(),
                updatedAccount.getAccountType(),
                List.copyOf(transactions)
        );
    }

    public boolean deleteById(String id) {
        String sql = """
                DELETE FROM accounts
                WHERE id = ?
                """;

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, id);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("SQL Error : " + e.getMessage());
        }

        return false;
    }

    private Account mapAccountWithoutTransactions(
            ResultSet resultSet
    ) throws SQLException {

        return new Account(
                resultSet.getString("id"),
                AccountType.valueOf(
                        resultSet.getString("account_type")
                ),
                List.of()
        );
    }
}