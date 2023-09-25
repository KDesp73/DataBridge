package kdesp73.databridge.connections;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MSAccessConnection implements DatabaseConnection {
    private Connection connection;

	/**
	 * Creates the connection with the database
	 * @param url driver connector (jdbc:ucanaccess://) + path to database
	 * @param username
	 * @param password
	 */
    @Override
    public void connect(String url, String username, String password) {
		if(!url.contains("jdbc:ucanaccess://")){
			url = "jdbc:ucanaccess://" + url;
		}

		try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            connection = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to connect to Microsoft Access database.");
        }
    }

	/**
	 * Executes the SQL query if it's valid
	 * @param query
	 * @return ResultSet
	 */
    @Override
    public ResultSet executeQuery(String query) {
        if(query.contains("INSERT") || query.contains("insert") || query.contains("UPDATE") || query.contains("update") || query.contains("DELETE") || query.contains("delete")){
			try {
				PreparedStatement statement = connection.prepareStatement(query);
				statement.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException("Error executing query: " + e.getMessage());
			}

			return null;
		}

		try {
            PreparedStatement statement = connection.prepareStatement(query);
            return statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error executing query: " + e.getMessage());
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

