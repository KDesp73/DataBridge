package io.github.kdesp73.databridge.connections;

import java.sql.*;
import io.github.kdesp73.databridge.helpers.Again;
import io.github.kdesp73.databridge.helpers.Config;
import io.github.kdesp73.databridge.helpers.SQLogger;

/**
 * The {@code OracleConnection} class implements the {@link DatabaseConnection} interface
 * for connecting to and interacting with an Oracle database.
 * <p>
 * This class provides methods to establish a connection, execute SQL queries (SELECT, UPDATE),
 * call Oracle functions and procedures, and close the connection. It also supports calling functions
 * that return values or tables and handles retries for failed operations using the {@link Again} helper.
 *
 * <p>
 * This class also logs errors related to database operations using the {@link SQLogger}.
 */
public class OracleConnection implements DatabaseConnection {

    private Connection connection;

    /**
     * Returns the underlying {@link Connection} object for the Oracle database.
     *
     * @return the {@link Connection} instance for the database connection.
     */
    @Override
    public Connection get() {
        return this.connection;
    }

    /**
     * Establishes a connection to the Oracle database.
     *
     * @param url the database URL in the format {@code jdbc:oracle:thin:@host:port:service}.
     * @param username the username for database access.
     * @param password the password for database access.
     * @throws SQLException if the connection to the database fails.
     */
    @Override
    public void connect(String url, String username, String password) throws SQLException {
        if (!url.startsWith("jdbc:oracle:thin:@")) {
            url = "jdbc:oracle:thin:@" + url;
        }
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            SQLogger.getLogger(SQLogger.LogLevel.ERRO, SQLogger.LogType.FILE).log(Config.getInstance().getLogLevel(),
                "Oracle Connection Failed", e);
            throw new SQLException("Oracle driver not found. Check https://mvnrepository.com/artifact/com.oracle.database.jdbc/ojdbc11 to get the latest version", e);
        }
    }

    /**
     * Executes an UPDATE SQL query (INSERT, UPDATE, DELETE) on the Oracle database.
     *
     * @param query the SQL query string to execute.
     * @return the number of rows affected by the query.
     * @throws SQLException if a database access error occurs or if the query is invalid.
     */
    @Override
    public int executeUpdate(String query) throws SQLException {
        return Again.retryWithDelay(() -> {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                return statement.executeUpdate();
            }
        }, Again.retries(), Again.delay());
    }

    /**
     * Executes a SELECT SQL query and returns the result as a {@link ResultSet}.
     *
     * @param query the SQL SELECT query string to execute.
     * @return a {@link ResultSet} containing the result of the query.
     * @throws SQLException if a database access error occurs or if the query is invalid.
     */
    @Override
    public ResultSet executeQuery(String query) throws SQLException {
        return Again.retryWithDelay(() -> {
            PreparedStatement statement = connection.prepareStatement(query);
            return statement.executeQuery();
        }, Again.retries(), Again.delay());
    }

    /**
     * Executes a DDL SQL query (e.g., CREATE, ALTER, DROP) on the Oracle database.
     *
     * @param query the SQL DDL query string to execute.
     * @return {@code true} if the query executed successfully, {@code false} otherwise.
     * @throws SQLException if a database access error occurs or if the query is invalid.
     */
    @Override
    public boolean execute(String query) throws SQLException {
        return Again.retryWithDelay(() -> {
            try (Statement statement = connection.createStatement()) {
                return statement.execute(query);
            }
        }, Again.retries(), Again.delay());
    }

    /**
     * Closes the database connection, releasing any resources.
     *
     * @throws SQLException if an error occurs while closing the connection.
     */
    @Override
    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    /**
     * Calls a Oracle function that returns a table of records.
     * <p>
     * This method prepares and executes a SQL query to call the specified Oracle function,
     * returning the result as a {@link ResultSet}.
     *
     * @param functionName the name of the Oracle function to call.
     * @param params the parameters to pass to the function.
     * @return a {@link ResultSet} containing the results of the function call. The caller is responsible
     *         for closing the {@link ResultSet}.
     * @throws SQLException if an error occurs during the function call.
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
                .log(Config.getInstance().getLogLevel(), "Error calling function " + functionName, e);
            throw new SQLException(e);
        }
    }

    /**
     * Calls a Oracle function that returns a single value.
     * <p>
     * This method prepares and executes a SQL call to the specified Oracle function, returning
     * the value of the result.
     *
     * @param functionName the name of the Oracle function to call.
     * @param returnType the JDBC return type of the function result.
     * @param params the parameters to pass to the function.
     * @return the result of the function call.
     * @throws SQLException if an error occurs during the function call.
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
                .log(Config.getInstance().getLogLevel(), "Error calling function " + functionName, e);
            throw new SQLException(e);
        }
    }

    /**
     * Calls a Oracle stored procedure.
     * <p>
     * This method prepares and executes a SQL call to the specified stored procedure.
     *
     * @param procedureName the name of the Oracle procedure to call.
     * @param params the parameters to pass to the procedure.
     * @throws SQLException if an error occurs during the procedure call.
     */
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
                .log(Config.getInstance().getLogLevel(), "Error calling procedure " + procedureName, e);
            throw new SQLException(e);
        }
    }

    /**
     * Validates whether the database connection is active.
     *
     * @throws SQLException if the connection is not active or established.
     */
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