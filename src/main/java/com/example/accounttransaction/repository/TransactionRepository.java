package com.example.accounttransaction.repository;

import com.example.accounttransaction.config.DatabaseConnectionFactory;
import com.example.accounttransaction.model.Transaction;
import com.example.accounttransaction.model.TransactionType;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TransactionRepository {

    private static final String COLUMNS =
            "id, created_at, transaction_type, amount, reason, account_id";

    private final DatabaseConnectionFactory connectionFactory;

    public TransactionRepository(DatabaseConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public List<Transaction> findAll() {
        String sql = "SELECT " + COLUMNS + " FROM transactions ORDER BY created_at ASC";
        List<Transaction> transactions = new ArrayList<>();

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                transactions.add(mapTransaction(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("SQL Error : " + e.getMessage());
        }

        return transactions;
    }

    public Transaction findById(String id) {
        String sql = "SELECT " + COLUMNS + " FROM transactions WHERE id = ?";

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapTransaction(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Error : " + e.getMessage());
        }

        return null;
    }

    public List<Transaction> findAllByAccountId(String accountId) {
        String sql = "SELECT " + COLUMNS
                + " FROM transactions WHERE account_id = ? ORDER BY created_at ASC";
        List<Transaction> transactions = new ArrayList<>();

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, accountId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    transactions.add(mapTransaction(resultSet));
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Error : " + e.getMessage());
        }

        return transactions;
    }

    public Transaction save(Transaction transaction) {
        String sql = """
                INSERT INTO transactions
                    (id, created_at, transaction_type, amount, reason, account_id)
                VALUES (?, ?, ?, ?, ?, ?)
                RETURNING id, created_at, transaction_type, amount, reason, account_id
                """;

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            setTransactionParameters(statement, transaction);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapTransaction(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Error : " + e.getMessage());
        }

        return null;
    }

    public Transaction update(Transaction transaction) {
        String sql = """
                UPDATE transactions
                SET created_at = ?, transaction_type = ?, amount = ?, reason = ?, account_id = ?
                WHERE id = ?
                RETURNING id, created_at, transaction_type, amount, reason, account_id
                """;

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setTimestamp(1, Timestamp.from(transaction.getCreatedAt()));
            statement.setString(2, transaction.getTransactionType().name());
            statement.setBigDecimal(3, transaction.getAmount());
            statement.setString(4, transaction.getReason());
            statement.setString(5, transaction.getAccountId());
            statement.setString(6, transaction.getId());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapTransaction(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Error : " + e.getMessage());
        }

        return null;
    }

    public boolean deleteById(String id) {
        String sql = "DELETE FROM transactions WHERE id = ?";

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("SQL Error : " + e.getMessage());
        }

        return false;
    }

    private void setTransactionParameters(
            PreparedStatement statement,
            Transaction transaction) throws SQLException {
        statement.setString(1, transaction.getId());
        statement.setTimestamp(2, Timestamp.from(transaction.getCreatedAt()));
        statement.setString(3, transaction.getTransactionType().name());
        statement.setBigDecimal(4, transaction.getAmount());
        statement.setString(5, transaction.getReason());
        statement.setString(6, transaction.getAccountId());
    }

    private Transaction mapTransaction(ResultSet resultSet) throws SQLException {
        return new Transaction(
                resultSet.getString("id"),
                resultSet.getTimestamp("created_at").toInstant(),
                TransactionType.valueOf(resultSet.getString("transaction_type")),
                resultSet.getBigDecimal("amount"),
                resultSet.getString("reason"),
                resultSet.getString("account_id")
        );
    }
}
