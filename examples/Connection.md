# Connection Examples

## Using AvailableConnections

Needs a `config.properties` file set-up appropriately

```java
try(DatabaseConnection conn = AvailableConnection.SQLITE.getConnection()) {
	ResultSet rs = conn.executeQuery("SELECT * FROM people");

	SQLogger.logResultSet(rs);

	rs.close();
} catch(SQLExcetpion e) {
	e.printStackTrace();
}
```

```java
try(PostgresConnection conn = AvailableConnection.POSTGRES.getConnection()) {
	ResultSet rs = conn.callFunction("function_name", param1, param2, param3, ...);

	SQLogger.logResultSet(rs);

	rs.close();
} catch(SQLException e) {
	e.printStackTrace();
}
```

## Using plain connections

```java
PostgresConnection conn = new PostgresConnection();
try {
	conn.connect("url", "username", "password");

	// Connection usage...

	conn.close();
} catch(SQLException e) {
	e.printStackTrace();
}

```


