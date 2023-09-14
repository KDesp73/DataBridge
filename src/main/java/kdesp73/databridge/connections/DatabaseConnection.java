package kdesp73.databridge.connections;

import java.sql.ResultSet;

public interface DatabaseConnection {
	public void connect(String url, String username, String password);
	ResultSet executeQuery(String query);
	void close();
}
