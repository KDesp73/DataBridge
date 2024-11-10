package kdesp73.databridge.connections;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import kdesp73.databridge.helpers.Again;

public class SQLiteConnection implements DatabaseConnection {

	private Connection connection;

	@Override
	public Connection get() {
		return this.connection;
	}

	/**
	 * Establishes a connection to the SQLite database.
	 *
	 * @param url Driver URL (jdbc:sqlite://) + path to database.
	 * @param username Unused for SQLite.
	 * @param password Unused for SQLite.
	 */
	@Override
	public void connect(String url, String username, String password) throws SQLException {
		if (!"jdbc:sqlite::memory:".equals(url) && !url.contains("jdbc:sqlite://")) {
			url = "jdbc:sqlite://" + url;
		}
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection(url);
		} catch (ClassNotFoundException e) {
			throw new SQLException("SQLite driver not found. Check https://mvnrepository.com/artifact/org.xerial/sqlite-jdbc/3.47.0.0 to get the latest version", e);
		}
	}

	@Override
	public int executeUpdate(String query) throws SQLException {
		if (!query.toLowerCase().matches("^(insert|update|delete).*")) {
			throw new IllegalArgumentException("Only INSERT, UPDATE, and DELETE statements are allowed.");
		}
		return Again.retryWithDelay(() -> {
			try (PreparedStatement statement = connection.prepareStatement(query)) {
				return statement.executeUpdate();
			}
		}, Again.retries(), Again.delay());
	}

	@Override
	public ResultSet executeQuery(String query) throws SQLException {
		if (!query.toLowerCase().startsWith("select")) {
			throw new IllegalArgumentException("Only SELECT statements are allowed.");
		}
		return Again.retryWithDelay(() -> {
			PreparedStatement statement = connection.prepareStatement(query);
			return statement.executeQuery();
		}, Again.retries(), Again.delay());
	}

	@Override
	public boolean execute(String query) throws SQLException {
		if (!query.toLowerCase().matches("^(create|alter|drop).*")) {
			throw new IllegalArgumentException("Only CREATE, ALTER, and DROP statements are allowed.");
		}
		return Again.retryWithDelay(() -> {
			try (Statement statement = connection.createStatement()) {
				return statement.execute(query);
			}
		}, Again.retries(), Again.delay());
	}

	@Override
	public void close() throws SQLException {
		if (connection != null && !connection.isClosed()) {
			connection.close();
		}
	}
}
