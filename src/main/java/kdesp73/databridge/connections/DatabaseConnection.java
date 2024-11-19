package kdesp73.databridge.connections;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The {@code DatabaseConnection} interface provides methods for interacting with
 * a relational database. It defines the contract for establishing a connection,
 * executing SQL queries (both SELECT and DDL/DML), and closing the connection.
 * <p>
 * Implementing classes must provide the actual behavior for connecting to the database,
 * executing queries, and managing the connection lifecycle. This interface also extends
 * {@link AutoCloseable} to ensure proper resource management by automatically closing
 * the connection when no longer needed.
 *
 * @author KDesp73
 */
public interface DatabaseConnection extends AutoCloseable {

    /**
     * Establishes a connection with the database using the provided credentials.
     *
     * @param url the database URL, typically in the format of the driver connector
     *            followed by the path to the database (e.g., jdbc:postgresql://localhost:5432/mydb).
     * @param username the username for database authentication.
     * @param password the password for database authentication.
     * @throws SQLException if a database access error occurs while establishing the connection.
     */
    void connect(String url, String username, String password) throws SQLException;

    /**
     * Executes a SELECT SQL query and returns the result as a {@link ResultSet}.
     *
     * @param query the SQL SELECT query string to execute.
     * @return a {@link ResultSet} containing the result of the query.
     * @throws SQLException if a database access error occurs or if the query is invalid.
     */
    ResultSet executeQuery(String query) throws SQLException;

    /**
     * Executes an UPDATE SQL query (e.g., INSERT, UPDATE, DELETE) and returns the number
     * of rows affected by the operation.
     *
     * @param query the SQL UPDATE query string to execute.
     * @return the number of rows affected by the update.
     * @throws SQLException if a database access error occurs or if the query is invalid.
     */
    int executeUpdate(String query) throws SQLException;

    /**
     * Executes a DDL SQL query (e.g., CREATE, ALTER, DROP) and returns whether the execution
     * was successful.
     *
     * @param query the SQL DDL query string to execute.
     * @return {@code true} if the DDL query executed successfully, {@code false} otherwise.
     * @throws SQLException if a database access error occurs or if the query is invalid.
     */
    boolean execute(String query) throws SQLException;

    /**
     * Closes the database connection and releases any associated resources.
     * <p>
     * This method is automatically invoked when the connection is no longer needed,
     * and the connection should be considered closed after this call.
     *
     * @throws SQLException if a database access error occurs while closing the connection.
     */
    @Override
    void close() throws SQLException;

    /**
     * Returns the plain {@link Connection} instance that represents the underlying
     * database connection.
     *
     * @return the {@link Connection} object representing the database connection.
     */
    Connection get();
}
