package com.example.accounttransaction.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class DatabaseConnectionFactory {

    private final String url;
    private final String username;
    private final String password;

    public DatabaseConnectionFactory(
            @Value("${app.database.url}") String url,
            @Value("${app.database.username}") String username,
            @Value("${app.database.password}") String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}
