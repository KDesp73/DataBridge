package kdesp73.databridge.connections;

import java.sql.ResultSet;

public interface DatabaseConnection extends AutoCloseable{
	/**
	 * Creates the connection with the database
	 * @param url driver connector + path to database
	 * @param username
	 * @param password
	 */
	public void connect(String url, String username, String password);

	/**
	 * Executes the SQL query if it's valid (For SELECT)
	 * @param query
	 * @return ResultSet
	 */
	ResultSet executeQuery(String query);

	/**
	 * Executes the SQL query if it's valid (For DMLs)
	 * @param query
	 * @return int Number of rows affected by the update
	 */
	int executeUpdate(String query);

	/**
	 * Executes the SQL query if it's valid (For DDLs)
	 * @param query
	 */
	void execute(String query);

	/**
	 * Close the connection with the database
	 */
	void close();
}
