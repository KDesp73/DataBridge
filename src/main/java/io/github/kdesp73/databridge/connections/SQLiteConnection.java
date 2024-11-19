package io.github.kdesp73.databridge.connections;

import java.sql.*;
import io.github.kdesp73.databridge.helpers.Again;

/**
 * The {@code SQLiteConnection} class implements the {@link DatabaseConnection} interface
 * for connecting to and interacting with an SQLite database.
 * <p>
 * This class provides methods to establish a connection to the SQLite database, execute SQL queries
 * (SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, DROP), and close the connection. It also supports retrying
 * operations on failure using the {@link Again} helper class.
 * </p>
 * <p>
 * The SQLite JDBC driver must be present to use this class. The connection URL must begin with {@code jdbc:sqlite://}
 * or be {@code jdbc:sqlite::memory:} for in-memory databases.
 * </p>
 *
 * @author KDesp73
 */
public class SQLiteConnection implements DatabaseConnection {

    private Connection connection;

    /**
     * Returns the underlying {@link Connection} object for the SQLite database.
     *
     * @return the {@link Connection} instance for the database connection.
     */
    @Override
    public Connection get() {
        return this.connection;
    }

    /**
     * Establishes a connection to the SQLite database.
     * <p>
     * The URL should be in the form {@code jdbc:sqlite://path_to_database}. For in-memory databases,
     * {@code jdbc:sqlite::memory:} should be used. If the URL does not start with {@code jdbc:sqlite://},
     * it will be prefixed automatically.
     * </p>
     *
     * @param url the database URL in the format {@code jdbc:sqlite://path_to_database}.
     * @param username Unused for SQLite.
     * @param password Unused for SQLite.
     * @throws SQLException if the connection to the database fails.
     */
    @Override
    public void connect(String url, String username, String password) throws SQLException {
        url = System.getProperty("user.dir") + "/" + url;
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

    /**
     * Executes an UPDATE SQL query (INSERT, UPDATE, DELETE) on the SQLite database.
     * <p>
     * The query is validated to ensure it is an INSERT, UPDATE, or DELETE statement before execution.
     * </p>
     *
     * @param query the SQL query string to execute.
     * @return the number of rows affected by the query.
     * @throws SQLException if a database access error occurs or if the query is invalid.
     * @throws IllegalArgumentException if the query is not an INSERT, UPDATE, or DELETE statement.
     */
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

    /**
     * Executes a SELECT SQL query and returns the result as a {@link ResultSet}.
     * <p>
     * The query is validated to ensure it is a SELECT statement before execution.
     * </p>
     *
     * @param query the SQL SELECT query string to execute.
     * @return a {@link ResultSet} containing the result of the query.
     * @throws SQLException if a database access error occurs or if the query is invalid.
     * @throws IllegalArgumentException if the query is not a SELECT statement.
     */
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

    /**
     * Executes a DDL SQL query (e.g., CREATE, ALTER, DROP) on the SQLite database.
     * <p>
     * The query is validated to ensure it is a CREATE, ALTER, or DROP statement before execution.
     * </p>
     *
     * @param query the SQL DDL query string to execute.
     * @return {@code true} if the query executed successfully, {@code false} otherwise.
     * @throws SQLException if a database access error occurs or if the query is invalid.
     * @throws IllegalArgumentException if the query is not a CREATE, ALTER, or DROP statement.
     */
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

    /**
     * Closes the database connection, releasing any resources.
     *
     * @throws SQLException if an error occurs while closing the connection.
     */
    @Override
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
