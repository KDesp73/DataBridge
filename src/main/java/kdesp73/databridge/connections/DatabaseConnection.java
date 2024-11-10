package kdesp73.databridge.connections;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface DatabaseConnection extends AutoCloseable {
    /**
     * Establishes a connection with the database.
     * @param url the database URL in the format of driver connector + path to database.
     * @param username the username for database access.
     * @param password the password for database access.
     * @throws SQLException if a database access error occurs.
     */
    void connect(String url, String username, String password) throws SQLException;

    /**
     * Executes a SELECT SQL query and returns the result.
     * @param query the SQL query string.
     * @return ResultSet containing the query results.
     * @throws SQLException if a database access error occurs or the query is invalid.
     */
    ResultSet executeQuery(String query) throws SQLException;

    /**
     * Executes an update SQL query (INSERT, UPDATE, or DELETE).
     * @param query the SQL query string.
     * @return the number of rows affected by the update.
     * @throws SQLException if a database access error occurs or the query is invalid.
     */
    int executeUpdate(String query) throws SQLException;

    /**
     * Executes a DDL SQL query (e.g., CREATE, ALTER, DROP).
     * @param query the SQL query string.
     * @throws SQLException if a database access error occurs or the query is invalid.
     */
    void execute(String query) throws SQLException;

    /**
     * Closes the database connection.
     * @throws SQLException if a database access error occurs.
     */
	@Override
    void close() throws SQLException;
	
	/**
	 * Returns the plain Connection instance
	 * @return Connection 
	 */
	Connection get();
}
