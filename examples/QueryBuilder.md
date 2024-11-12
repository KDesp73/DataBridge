# QueryBuilder Examples

[Documentation](https://kdesp73.github.io/DataBridge-Documentation/dd/ddc/classkdesp73_1_1databridge_1_1helpers_1_1QueryBuilder.html)

```java
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
```
