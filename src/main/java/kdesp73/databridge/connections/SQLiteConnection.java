package kdesp73.databridge.connections;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteConnection implements DatabaseConnection {
    private Connection connection;

    /**
	 * Creates the connection with the database
	 * @param url driver connector (jdbc:sqlite://) + path to database
	 * @param username
	 * @param password
	 */
	@Override
    public void connect(String url, String username, String password) {
		if(!url.contains("jdbc:sqlite://")){
			url = "jdbc:sqlite:" + url;
		}

		try {
			Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to connect to SQLite database.");
        }
    }

	/**
	 * Executes the SQL query if it's valid (For INSERT, UPDATE, DELETE etc)
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
			// You can handle SQLException differently based on your needs.
			throw new RuntimeException("Error executing query: " + e.getMessage());
		}
	}

	/**
	 * Executes the SQL query if it's valid
	 * @param query
	 * @return ResultSet
	 */
    @Override
    public ResultSet executeQuery(String query) {
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            return statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error executing query: " + e.getMessage());
        }
    }

	/**
	 * Executes the SQL query if it's valid (For DDLs)
	 * @param query
	 */
	@Override
	public void execute(String query) {
		try (Statement statement = connection.createStatement()) {
			if (query.toLowerCase().contains("create") ||
					query.toLowerCase().contains("alter") ||
					query.toLowerCase().contains("drop")) {
				statement.execute(query);
				System.out.println("DDL statement executed successfully.");
			} else {
				throw new IllegalArgumentException("Only CREATE, ALTER, and DROP statements are allowed.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			// You can handle SQLException differently based on your needs.
			throw new RuntimeException("Error executing DDL statement: " + e.getMessage());
		}
	}

	/**
	 * Close the connection with the database
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
