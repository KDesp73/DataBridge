package kdesp73.madb.connections;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MSAccessConnection implements DatabaseConnection {
    private Connection connection;

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

