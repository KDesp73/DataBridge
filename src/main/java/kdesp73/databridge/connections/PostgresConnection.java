package kdesp73.databridge.connections;

import java.sql.*;
import kdesp73.databridge.helpers.SQLogger;
import kdesp73.databridge.helpers.SQLogger.LogLevel;

public class PostgresConnection implements DatabaseConnection {
	private Connection connection;

	@Override
	public Connection get() {
		return this.connection;
	}
	
	@Override
	public void connect(String url, String username, String password) throws SQLException {
		if (!url.startsWith("jdbc:postgresql://")) {
			url = "jdbc:postgresql://" + url;
		}
		try {
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection(url, username, password);
		} catch (ClassNotFoundException e) {
			SQLogger.getLogger(SQLogger.LogLevel.ERRO, SQLogger.LogType.FILE).log("Postgres Connection Failed", e);
			throw new SQLException("PostgreSQL driver not found. Check https://mvnrepository.com/artifact/org.postgresql/postgresql to get the latest version", e);
		}
	}

	@Override
	public int executeUpdate(String query) throws SQLException {
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			return statement.executeUpdate();
		}
	}

	@Override
	public ResultSet executeQuery(String query) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(query);
		return statement.executeQuery();
	}

	@Override
	public void execute(String query) throws SQLException {
		try (Statement statement = connection.createStatement()) {
			statement.execute(query);
		}
	}

	@Override
	public void close() throws SQLException {
		if (connection != null) {
			connection.close();
		}
	}

	/**
	 * Calls a Postgresql function that returns a table / records
 	 * 
	 * @param functionName 
	 * @param params
	 * @return ResultSet NOTE: caller is responsible for closing the ResultSet
	 * @throws SQLException 
	 */
	public ResultSet callFunction(String functionName, Object... params) throws SQLException {
		validateConnection();
		StringBuilder sql = new StringBuilder("SELECT * FROM ").append(functionName).append("(");
		for (int i = 0; i < params.length; i++) {
			sql.append("?");
			if (i < params.length - 1) {
				sql.append(", ");
			}
		}
		sql.append(")");

		try {
			PreparedStatement stmt = connection.prepareStatement(sql.toString());
			for (int i = 0; i < params.length; i++) {
				stmt.setObject(i + 1, params[i]);
			}
			return stmt.executeQuery();
		} catch (SQLException e) {
			SQLogger.getLogger(SQLogger.LogLevel.ERRO, SQLogger.LogType.FILE)
				.log("Error calling function " + functionName, e);
			throw new SQLException(e);
		}
	}

	/**
	 * Calls a Postgresql function that returns a value
	 * 
	 * @param functionName
	 * @param returnType
	 * @param params
	 * @return
	 * @throws SQLException 
	 */
	public Object callFunctionValue(String functionName, int returnType, Object... params) throws SQLException {
		validateConnection();
		StringBuilder sql = new StringBuilder("{ ? = call ").append(functionName).append("(");
		for (int i = 0; i < params.length; i++) {
			sql.append("?");
			if (i < params.length - 1) {
				sql.append(", ");
			}
		}
		sql.append(") }");

		try (CallableStatement stmt = this.connection.prepareCall(sql.toString())) {
			stmt.registerOutParameter(1, returnType);
			for (int i = 0; i < params.length; i++) {
				stmt.setObject(i + 2, params[i]);
			}
			stmt.execute();
			return stmt.getObject(1);
		} catch (SQLException e) {
			SQLogger.getLogger(SQLogger.LogLevel.ERRO, SQLogger.LogType.FILE)
				.log("Error calling function " + functionName, e);
			throw new SQLException(e);
		}
	}

	public void callProcedure(String procedureName, Object... params) throws SQLException {
		validateConnection();
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
				stmt.setObject(i + 1, params[i]);
			}
			stmt.execute();
		} catch (SQLException e) {
			SQLogger.getLogger(SQLogger.LogLevel.ERRO, SQLogger.LogType.FILE)
				.log("Error calling procedure " + procedureName, e);
			throw new SQLException(e);
		}
	}

	private void validateConnection() {
		try {
			if (connection == null || connection.isClosed()) {
				throw new SQLException("No active database connection.");
			}
		} catch (SQLException e) {
			throw new RuntimeException("Connection validation failed", e);
		}
	}
}
