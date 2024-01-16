# DataBridge

> previously called `MADB`

A multi-driver database connection tool equipped with a query builder and a ResultSet parser and visualizer

## Documentation

You can find it [here](https://kdesp73.github.io/DataBridge-Documentation)

## Supported Databases

- Microsoft Access Database
- SQLite

More soon to be added

## Example Usage

### Create your own connector

<div class="mockup-code">
```java
public class PostgresConnection implements DatabaseConnection{
	// Implement the necessary methods

	public void connect(String url, String username, String password){
		// implementation
	}

	public ResultSet executeQuery(String query){
		// implementation
	}

	public int executeUpdate(String query){
		// implementation
	}

	public void execute(String query){
		// implementation
	}

	public void close(){
		// implementation
	}

}
```
</div>


*See the library's DatabaseConnections for help*

### QueryBuilder

```java
public static void main(String[] args) {
        QueryBuilder queryBuilder = new QueryBuilder();

        // SELECT query
        String selectQuery = queryBuilder.select("id", "name")
                .from("users")
                .where("age > 30")
                .build();

        // INSERT query
        String insertQuery = queryBuilder.insertInto("products")
                .columns("name", "price")
                .values("Product 1", "10.99")
                .build();

        // UPDATE query
        String updateQuery = queryBuilder.update("products")
                .set("price", "15.99")
                .where("id = 1")
                .build();

        // DELETE query
        String deleteQuery = queryBuilder.deleteFrom("orders")
                .where("customer_id = 5")
                .build();

        System.out.println("SELECT query: " + selectQuery);
        System.out.println("INSERT query: " + insertQuery);
        System.out.println("UPDATE query: " + updateQuery);
        System.out.println("DELETE query: " + deleteQuery);
    }
```

### Create a connection with the Database

```java
public static void main(String[] args) {
	String dbUrl = "jdbc:ucanaccess://" + "path_to_the_db";
	String dbUsername = ""; // if necessary
	String dbPassword = ""; // if necessary

	// Create an instance of MSAccessConnection
	MSAccessConnection msAccessConnection = new MSAccessConnection();

	try {
		msAccessConnection.connect(dbUrl, dbUsername, dbPassword);

		// Execute your query

	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		msAccessConnection.close();
	}
}
```

### ResultProcessor

This class helps the user at accessing the data from the ResultSet object in a more intuitive way

#### toList

```java
QueryBuilder qb = new QueryBuilder();
ResultPrecessor rp = new ResultProcessor();

String query = "SELECT * FROM Settings";
// or
String query1 = qb.select().from("Settings").build();

ResultSet rs = msAccessConnection.executeQuery(query);

List<ResultRow> table = rp.toList(resultSet);

for(ResultRow row : table) {
	String id = row.get("id");
	String theme = row.get("theme");

	// Perform operations with the data
}
```

#### printTable

Continuing the previous exaple...

```java
ResultPrecessor rp = new ResultProcessor();

rp.printTable(table);
```

## Dependency

```xml
<dependency>
	<groupId>kdesp73</groupId>
	<artifactId>DataBridge</artifactId>
	<version>2.0.7</version>
</dependency>
```

Add this to your pom.xml file and then download the jar into the `.m2/repository/kdesp73/databridge/2.0.7/` folder

## Contributing

Contributions are always welcome!

See [Contributing.md](https://github.com/KDesp73/DataBridge/blob/main/Contributing.md) for ways to get started.

Please adhere to this project's [Code of Conduct](https://github.com/KDesp73/DataBridge/blob/main/CODE_OF_CONDUCT.md).

## Authors

- [@KDesp73](https://github.com/KDesp73)

## Credits

- [ucanaccess](https://ucanaccess.sourceforge.net/site.html)
- [sqlite-jdbc](https://github.com/xerial/sqlite-jdbc)

## License

[MIT](https://choosealicense.com/licenses/mit/)

