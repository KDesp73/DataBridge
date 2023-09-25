package kdesp73.databridge.connections;

import java.sql.ResultSet;

public interface DatabaseConnection {
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
	 * Executes the SQL query if it's valid (For INSERT, UPDATE, DELETE etc)
	 * @param query
	 * @return
	 */
	int executeUpdate(String query);

	/**
	 * Close the connection with the database
	 */
	void close();
}
