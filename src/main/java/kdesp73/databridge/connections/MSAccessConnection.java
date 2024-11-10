package kdesp73.databridge.connections;

import java.sql.*;

public class MSAccessConnection implements DatabaseConnection {
    private Connection connection;

	@Override
	public Connection get() {
		return this.connection;
	}
	
    /**
     * Creates the connection with the database.
     *
     * @param url driver connector (jdbc:ucanaccess://) + path to database
     * @param username the username for database access.
     * @param password the password for database access.
     * @throws SQLException if the database connection fails.
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
     * Executes an update query (INSERT, UPDATE, or DELETE).
     *
     * @param query the SQL query string.
     * @return the number of rows affected.
     * @throws SQLException if a database access error occurs or the query is invalid.
     */
    @Override
    public int executeUpdate(String query) throws SQLException {
        validateConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            return statement.executeUpdate();
        }
    }

    /**
     * Executes a SELECT query and returns the result.
     *
     * @param query the SQL query string.
     * @return ResultSet containing the query results.
     * @throws SQLException if a database access error occurs or the query is invalid.
     */
    @Override
    public ResultSet executeQuery(String query) throws SQLException {
        validateConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        return statement.executeQuery();
    }

    /**
     * Executes a DDL query (e.g., CREATE, ALTER, DROP).
     *
     * @param query the SQL query string.
     * @throws SQLException if a database access error occurs or the query is invalid.
     */
    @Override
    public void execute(String query) throws SQLException {
        validateConnection();
        try (Statement statement = connection.createStatement()) {
            statement.execute(query);
        }
    }

    /**
     * Closes the database connection.
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
     * Validates if the connection is active.
     *
     * @throws SQLException if the connection is not established.
     */
    private void validateConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("No active database connection.");
        }
    }
}
