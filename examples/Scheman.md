# Scheman Examples

[Documentation](https://kdesp73.github.io/DataBridge-Documentation/d1/d60/classkdesp73_1_1databridge_1_1migration_1_1Scheman.html)

## CLI

The following code gives you acces to the Scheman cli tool for managing migrations

```java
public static void main(String[] args) {
	try(DatabaseConnection conn = AvailableConnections.SQLITE.getConnection()) {
		new Scheman(conn).cli();
	} catch(SQLException e){
		e.printStackTrace();
	}
}
```

## Interface

```java
// Assuming conn is defined above
Scheman scheman = new Scheman(conn);

scheman.runMigrations(); // Runs all pending migrations

scheman.applyMigration(new Migration("<path>")); // Apply specific migration script

scheman.rollbackMigration(); // Rolls back the last migration

scheman.rerunMigrations(); // Runs any migrations that has changed since being applied
```
