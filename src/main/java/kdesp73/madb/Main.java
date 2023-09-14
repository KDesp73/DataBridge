package kdesp73.madb;

import java.sql.ResultSet;
import java.util.List;

import kdesp73.madb.connections.MSAccessConnection;
import kdesp73.madb.helpers.QueryBuilder;
import kdesp73.madb.helpers.ResultProcessor;
import kdesp73.madb.helpers.ResultRow;

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

			QueryBuilder qb = new QueryBuilder();
			ResultProcessor rp = new ResultProcessor();

			// Execute a sample query
			String query = qb.select().from("Settings").build();
			System.out.println(query);
			ResultSet resultSet = msAccessConnection.executeQuery(query);

			List<ResultRow> table = rp.toList(resultSet);

			for (ResultRow row : table) {
				String font = row.get("Font");
				String theme = row.get("Theme");

				// Perform operations with the data
				System.out.println("Font: " + font + ", Theme: " + theme);
			}

			System.out.println("");

			rp.printTable(table);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// Close the database connection when done
			msAccessConnection.close();
		}
	}
}
