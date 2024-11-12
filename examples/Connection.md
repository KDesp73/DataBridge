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


