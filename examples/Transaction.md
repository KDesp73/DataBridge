# Transaction Examples

[Documentation](https://kdesp73.github.io/DataBridge-Documentation/da/dd8/classkdesp73_1_1databridge_1_1helpers_1_1Transaction.html)

```java
Transaction transaction = new Transaction(conn);

transaction.begin();

conn.executeUpdate(new QueryBuilder().deleteFrom("people").build());

if(deleteAll)
	transaction.commit();
else
	transaction.rollback();


transaction.end();

```
