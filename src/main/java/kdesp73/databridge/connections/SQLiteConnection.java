package kdesp73.databridge.connections;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLiteConnection implements DatabaseConnection {
    private Connection connection;

    @Override
    public void connect(String url, String username, String password) {
		if(!url.contains("jdbc:sqlite:")){
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
