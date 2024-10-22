package kdesp73.databridge.connections;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PostgresConnection implements DatabaseConnection {
	private Connection connection;

	/**
	 * Creates the connection with the database
	 * @param url JDBC URL for PostgreSQL
	 * @param username PostgreSQL username
	 * @param password PostgreSQL password
	 */
	@Override
	public void connect(String url, String username, String password) {
		if (!url.startsWith("jdbc:postgresql://")) {
			url = "jdbc:postgresql://" + url;
		}

		try {
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection(url, username, password);
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to connect to PostgreSQL database.");
		}
	}

	/**
	 * Executes the SQL query for INSERT, UPDATE, DELETE, etc.
	 */
	@Override
	public int executeUpdate(String query) {
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			if (query.toLowerCase().contains("insert") || query.toLowerCase().contains("update")
					|| query.toLowerCase().contains("delete")) {
				return statement.executeUpdate();
			} else {
				throw new IllegalArgumentException("Only INSERT, UPDATE, and DELETE statements are allowed.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Error executing query: " + e.getMessage());
		}
	}

	/**
	 * Executes a SQL SELECT query
	 */
	@Override
	public ResultSet executeQuery(String query) {
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			if (query.toLowerCase().contains("select")) {
				return statement.executeQuery();
			} else {
				throw new IllegalArgumentException("Only SELECT statements are allowed.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Error executing query: " + e.getMessage());
		}
	}

	/**
	 * Executes DDL statements such as CREATE, ALTER, DROP
	 */
	@Override
	public void execute(String query) {
		try (Statement statement = connection.createStatement()) {
			if (query.toLowerCase().contains("create") || query.toLowerCase().contains("alter") ||
					query.toLowerCase().contains("drop")) {
				statement.execute(query);
				System.out.println("DDL statement executed successfully.");
			} else {
				throw new IllegalArgumentException("Only CREATE, ALTER, and DROP statements are allowed.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Error executing DDL statement: " + e.getMessage());
		}
	}

	/**
	 * Closes the database connection
	 */
	@Override
	public void close() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Error closing database connection: " + e.getMessage());
		}
	}
}

