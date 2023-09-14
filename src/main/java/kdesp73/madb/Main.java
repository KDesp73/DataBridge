package kdesp73.madb;

import java.sql.ResultSet;

import kdesp73.madb.connections.MSAccessConnection;

public class Main {
	public static void main(String[] args) {
		String dbUrl = "jdbc:ucanaccess:///home/konstantinos/personal/repos/java/Watchlist-Wizard/MovieManager/data/MoviesDatabase.accdb";
        String dbUsername = "";
        String dbPassword = "";

        // Create an instance of MSAccessConnection
        MSAccessConnection msAccessConnection = new MSAccessConnection();

        try {
            // Connect to the Microsoft Access database
            msAccessConnection.connect(dbUrl, dbUsername, dbPassword);

            // Execute a sample query
            String query = "SELECT * FROM Settings";
            ResultSet resultSet = msAccessConnection.executeQuery(query);

            // Process the query results
            while (resultSet.next()) {
                // Retrieve data from the result set

                String name = resultSet.getString("Theme");

                // Do something with the data (e.g., print it)
                System.out.println("Theme: " + name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close the database connection when done
            msAccessConnection.close();
        }
	}
}
