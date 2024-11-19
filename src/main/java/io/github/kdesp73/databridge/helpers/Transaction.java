package io.github.kdesp73.databridge.helpers;

import java.sql.Connection;
import java.sql.SQLException;
import io.github.kdesp73.databridge.connections.DatabaseConnection;

/**
 * The {@code Transaction} class is responsible for managing database transactions.
 * It supports beginning, committing, and rolling back transactions using a provided {@link DatabaseConnection}.
 * This class ensures that the transaction is properly managed, with methods to handle active transaction states,
 * commit or roll back changes, and clean up after transactions.
 *
 * @author KDesp73
 */
public class Transaction {

    /**
     * The database connection used to manage the transaction.
     */
    private Connection connection;

    /**
     * Flag indicating whether a transaction is currently active.
     */
    private boolean transactionActive = false;

    /**
     * Constructs a {@code Transaction} object using the provided {@code DatabaseConnection}.
     * This constructor initializes the connection and sets auto-commit to false,
     * preparing it for transaction management.
     *
     * @param dbConnection the database connection to use for the transaction.
     * @throws SQLException if a database access error occurs or the connection is invalid.
     */
    public Transaction(DatabaseConnection dbConnection) throws SQLException {
        this.connection = dbConnection.get();
        this.connection.setAutoCommit(false);
    }

    /**
     * Starts a new transaction. This method ensures that no other transaction is currently active.
     * If a transaction is already active, an {@code SQLException} will be thrown.
     *
     * @throws SQLException if a transaction is already active.
     */
    public void begin() throws SQLException {
        if (transactionActive) {
            throw new SQLException("Transaction already active");
        }
        transactionActive = true;
    }

    /**
     * Commits the current transaction, making all changes permanent in the database.
     * If no active transaction exists, an {@code SQLException} will be thrown.
     *
     * @throws SQLException if no active transaction exists or if the commit fails.
     */
    public void commit() throws SQLException {
        if (!transactionActive) {
            throw new SQLException("No active transaction to commit");
        }
        try {
            connection.commit();
        } finally {
            end();
        }
    }

    /**
     * Rolls back the current transaction, discarding all changes made during the transaction.
     * If no active transaction exists, an {@code SQLException} will be thrown.
     *
     * @throws SQLException if no active transaction exists or if the rollback fails.
     */
    public void rollback() throws SQLException {
        if (!transactionActive) {
            throw new SQLException("No active transaction to roll back");
        }
        try {
            connection.rollback();
        } finally {
            end();
        }
    }

    /**
     * Ends the current transaction by resetting the auto-commit mode of the connection
     * and marking the transaction as inactive.
     *
     * @throws SQLException if an error occurs while resetting auto-commit.
     */
    private void end() throws SQLException {
        transactionActive = false;
        connection.setAutoCommit(true);
    }

    /**
     * Closes the transaction manager, ensuring any active transaction is rolled back and
     * the database connection is closed. If no active transaction exists, no rollback is performed.
     *
     * @throws SQLException if an error occurs during rollback or closing the connection.
     */
    public void close() throws SQLException {
        if (transactionActive) {
            rollback();  // Ensures any open transaction is rolled back
        }
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
