package io.github.kdesp73.databridge.connections;

import java.sql.*;
import io.github.kdesp73.databridge.helpers.Again;

/**
 * The {@code MSAccessConnection} class implements the {@link DatabaseConnection} interface
 * for connecting to and interacting with a Microsoft Access database using the UCanAccess JDBC driver.
 * <p>
 * This class provides methods to establish a connection to the database, execute SQL queries (both
 * SELECT and UPDATE), handle DDL operations, and close the connection.
 * <p>
 * The connection to the database is established using the UCanAccess JDBC driver, which requires
 * the driver to be present in the classpath. The connection string must follow the format
 * {@code jdbc:ucanaccess://path_to_database}.
 *
 * @author KDesp73
 */
public class MSAccessConnection implements DatabaseConnection {

    private Connection connection;

    /**
     * Returns the underlying {@link Connection} object for the MS Access database.
     *
     * @return the {@link Connection} instance for the database connection.
     */
    @Override
    public Connection get() {
        return this.connection;
    }

    /**
     * Establishes a connection to the MS Access database.
     * <p>
     * The connection URL must begin with {@code jdbc:ucanaccess://}, followed by the path to the database file.
     * If the URL does not start with this prefix, it will be automatically added.
     *
     * @param url the database URL in the format {@code jdbc:ucanaccess://path_to_database}.
     * @param username the username for database access (not used by MS Access, but required for the interface).
     * @param password the password for database access (not used by MS Access, but required for the interface).
     * @throws SQLException if the connection to the database fails.
     */
    @Override
    public void connect(String url, String username, String password) throws SQLException {
        if (!url.startsWith("jdbc:ucanaccess://")) {
            url = "jdbc:ucanaccess://" + url;
        }

        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            connection = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            throw new SQLException("UCanAccess driver not found. Check https://mvnrepository.com/artifact/net.sf.ucanaccess/ucanaccess to get the latest version", e);
        }
    }

    /**
     * Executes an UPDATE SQL query (INSERT, UPDATE, DELETE) on the MS Access database.
     * <p>
     * This method attempts the query execution with retries in case of failure, using the {@link Again} helper.
     *
     * @param query the SQL query string to execute.
     * @return the number of rows affected by the query.
     * @throws SQLException if a database access error occurs or if the query is invalid.
     */
    @Override
    public int executeUpdate(String query) throws SQLException {
        validateConnection();
        return Again.retryWithDelay(() -> {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                return statement.executeUpdate();
            }
        }, Again.retries(), Again.delay());
    }

    /**
     * Executes a SELECT SQL query and returns the result as a {@link ResultSet}.
     * <p>
     * This method attempts the query execution with retries in case of failure, using the {@link Again} helper.
     *
     * @param query the SQL SELECT query string to execute.
     * @return a {@link ResultSet} containing the result of the query.
     * @throws SQLException if a database access error occurs or if the query is invalid.
     */
    @Override
    public ResultSet executeQuery(String query) throws SQLException {
        validateConnection();
        return Again.retryWithDelay(() -> {
            PreparedStatement statement = connection.prepareStatement(query);
            return statement.executeQuery();
        }, Again.retries(), Again.delay());
    }

    /**
     * Executes a DDL SQL query (e.g., CREATE, ALTER, DROP) on the MS Access database.
     * <p>
     * This method attempts the query execution with retries in case of failure, using the {@link Again} helper.
     *
     * @param query the SQL DDL query string to execute.
     * @return {@code true} if the query executed successfully, {@code false} otherwise.
     * @throws SQLException if a database access error occurs or if the query is invalid.
     */
    @Override
    public boolean execute(String query) throws SQLException {
        validateConnection();
        return Again.retryWithDelay(() -> {
            try (Statement statement = connection.createStatement()) {
                return statement.execute(query);
            }
        }, Again.retries(), Again.delay());
    }

    /**
     * Closes the database connection, releasing any resources.
     * <p>
     * If the connection is already {@code null} or closed, this method does nothing.
     */
    @Override
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }

    /**
     * Validates whether the database connection is active.
     * <p>
     * If the connection is {@code null} or closed, this method throws an {@link SQLException}.
     *
     * @throws SQLException if the connection is not active or established.
     */
    private void validateConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("No active database connection.");
        }
    }
}
