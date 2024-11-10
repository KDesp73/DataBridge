package kdesp73.databridge.helpers;

import java.sql.Connection;
import java.sql.SQLException;
import kdesp73.databridge.connections.DatabaseConnection;

public class TransactionManager {

	private Connection connection;
	private boolean transactionActive = false;

	public TransactionManager(DatabaseConnection dbConnection) throws SQLException {
		this.connection = dbConnection.get();
		this.connection.setAutoCommit(false);
	}

	/**
	 * Starts a new transaction.
	 * @throws java.sql.SQLException
	 */
	public void begin() throws SQLException {
		if (transactionActive) {
			throw new SQLException("Transaction already active");
		}
		transactionActive = true;
	}

	/**
	 * Commits the current transaction.
	 * @throws java.sql.SQLException
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
	 * Rolls back the current transaction.
	 * @throws java.sql.SQLException
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
	 * Ends the transaction by resetting auto-commit.
	 */
	private void end() throws SQLException {
		transactionActive = false;
		connection.setAutoCommit(true);
	}

	/**
	 * Closes the transaction manager, ending any active transaction.
	 * @throws java.sql.SQLException
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
