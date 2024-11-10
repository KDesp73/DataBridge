package kdesp73.databridge.connections;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import kdesp73.databridge.helpers.SQLogger;

public class PostgresConnection implements DatabaseConnection {

	public Connection connection;

	/**
	 * Creates the connection with the database
	 *
	 * @param url JDBC URL for PostgreSQL
	 * @param username PostgreSQL username
	 * @param password PostgreSQL password
	 */
	@Override
	public void connect(String url, String username, String password) {
		if (!url.startsWith("jdbc:postgresql://")) {
			url = "jdbc:postgresql://" + url;
		}

		try {
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection(url, username, password);
		} catch (SQLException | ClassNotFoundException e) {
			SQLogger.getLogger(SQLogger.LogLevel.ERRO, SQLogger.LogType.FILE).log("Connection to Postgres Database failed", e);
			throw new RuntimeException("Failed to connect to PostgreSQL database.");
		}
	}

	/**
	 * Executes the SQL query for INSERT, UPDATE, DELETE.
	 */
	@Override
	public int executeUpdate(String query) {
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			if (query.toLowerCase().contains("insert") || query.toLowerCase().contains("update")
				|| query.toLowerCase().contains("delete")) {
				return statement.executeUpdate();
			} else {
				throw new IllegalArgumentException("Only INSERT, UPDATE, and DELETE statements are allowed.");
			}
		} catch (SQLException e) {
			SQLogger.getLogger(SQLogger.LogLevel.ERRO, SQLogger.LogType.FILE).log("Error execuring query", e);
			throw new RuntimeException("Error executing query: " + e.getMessage());
		}
	}

	/**
	 * Executes a SQL SELECT query
	 */
	@Override
	public ResultSet executeQuery(String query) {
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			if (query.toLowerCase().contains("select")) {
				return statement.executeQuery();
			} else {
				throw new IllegalArgumentException("Only SELECT statements are allowed.");
			}
		} catch (SQLException e) {
			SQLogger.getLogger(SQLogger.LogLevel.ERRO, SQLogger.LogType.FILE).log("Failed executing query", e);
			throw new RuntimeException("Error executing query: " + e.getMessage());
		}
	}

	/**
	 * Executes any SQL statement.
	 */
	@Override
	public void execute(String query) {
		try (Statement statement = connection.createStatement()) {
			statement.execute(query);
		} catch (SQLException e) {
			SQLogger.getLogger(SQLogger.LogLevel.ERRO, SQLogger.LogType.FILE).log("Error executing DDL statement", e);
			throw new RuntimeException("Error executing DDL statement: " + e.getMessage());
		}
	}

	/**
	 * Closes the database connection
	 */
	@Override
	public void close() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			SQLogger.getLogger(SQLogger.LogLevel.ERRO, SQLogger.LogType.FILE).log("Failed closing database connection", e);
			throw new RuntimeException("Error closing database connection: " + e.getMessage());
		}
	}

	public ResultSet callFunction(String functionName, Object... params) {
		ResultSet rs = null;
		StringBuilder sql = new StringBuilder("SELECT * FROM ").append(functionName).append("(");

		// Append placeholders for parameters
		for (int i = 0; i < params.length; i++) {
			sql.append("?");
			if (i < params.length - 1) {
				sql.append(", ");
			}
		}
		sql.append(")");

		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement(sql.toString());
			// Set the parameters
			for (int i = 0; i < params.length; i++) {
				stmt.setObject(i + 1, params[i]); // Parameters start from index 1 in JDBC
			}

			rs = stmt.executeQuery();
		} catch (SQLException e) {
			SQLogger.getLogger(SQLogger.LogLevel.ERRO, SQLogger.LogType.FILE).log("Failed calling function " + functionName, e);
			throw new RuntimeException(e.getMessage());
		}

		return rs; // NOTE: The caller is responsible for closing the ResultSet and PreparedStatement
	}

	public Object callFunctionValue(String functionName, int returnType, Object... params) {
		StringBuilder sql = new StringBuilder("{ ? = call ").append(functionName).append("(");

		// Add commas for each parameter placeholder, if any
		for (int i = 0; i < params.length; i++) {
			sql.append("?");
			if (i < params.length - 1) {
				sql.append(", ");
			}
		}

		sql.append(") }");

		try (CallableStatement stmt = this.connection.prepareCall(sql.toString())) {
			// Register the return parameter with the specified return type
			stmt.registerOutParameter(1, returnType);

			// Set each parameter
			for (int i = 0; i < params.length; i++) {
				stmt.setObject(i + 2, params[i]);  // Parameters start from index 2
			}

			stmt.execute();

			// Retrieve and return the result based on the return type
			return switch (returnType) {
				case Types.INTEGER ->
					stmt.getInt(1);
				case Types.VARCHAR ->
					stmt.getString(1);
				case Types.BOOLEAN ->
					stmt.getBoolean(1);
				case Types.DOUBLE ->
					stmt.getDouble(1);
				default ->
					stmt.getObject(1);
			};
		} catch (SQLException e) {
			SQLogger.getLogger(SQLogger.LogLevel.ERRO, SQLogger.LogType.FILE).log("Failed calling function " + functionName, e);
			throw new RuntimeException(e.getMessage());
		}
	}

	public void callProcedure(String procedureName, Object... params) {
		StringBuilder sql = new StringBuilder("CALL ").append(procedureName).append("(");

		for (int i = 0; i < params.length; i++) {
			sql.append("?");
			if (i < params.length - 1) {
				sql.append(", ");
			}
		}
		sql.append(")");

		try (CallableStatement stmt = connection.prepareCall(sql.toString())) {
			for (int i = 0; i < params.length; i++) {
				stmt.setObject(i + 1, params[i]);  // Parameters start from index 1 in JDBC
			}

			stmt.execute();

		} catch (SQLException e) {
			SQLogger.getLogger(SQLogger.LogLevel.ERRO, SQLogger.LogType.FILE).log("Failed calling procedure " + procedureName, e);
			throw new RuntimeException(e.getMessage());
		}
	}
}
