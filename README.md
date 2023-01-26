
# MADB - Java Methods to Manage a Microsoft Access Database (.accdb)

## Tutorial

1) Import MADB in your project as a dependency (see [Releases](https://github.com/KDesp73/MADB/releases/tag/MADBv.1.0.2) for .jar file)
2) Create MADB object

```java
MADB db = new MADB([your_db_directory]);
```

3) Now through this object you have access to all of the SQL Methods (INSERT, SELECT, UPDATE etc)

## Examples

```java
//Insert value at the specific column
db.INSERT("Table_name", "Column_name", value);

//Insert multiple values in multiple columns
db.INSERT("Table_name", new String[]{"Column_name_1", "Column_name_2", "Column_name_3"}, new String[]{value1, value2, value3});

//Select the whole column
ArrayList<Object> arr = db.SELECT("Table_name", "Column_name");

//Update a value based on the custom condition
db.UPDATE("Table_name", "Column_name", value, new Condition("Column", Value, Operator.AND, "Other_Column", Other_Value));

//Delete a row based on the custom condition
db.DELETE("Table_name", "Column_name", new Condition("Column_name", Value));
```

## Creating Conditions

Example - Create the condition: ColumnA = ValueA AND NOT ColumnB = ValueB
```java
Condition c = new Condition("ColumnA", ValueA, Operator.AND_NOT, "ColumnB", ValueB);
```

## Available Operators
* OR
* AND
* NOT
* AND_NOT
* OR_NOT

## Dependencies

```xml
<dependency>
    <groupId>MADB</groupId>
    <artifactId>accessDB</artifactId>
    <version>1.0.4-SNAPSHOT</version>
</dependency>
```


## Clone

```bash
git clone https://github.com/KDesp73/MADB
```

## TODO
* Create custom conditions
* Make dependency fully public on the Maven repository

## Contributing

Contributions are always welcome!

See [Contributing.md](https://github.com/KDesp73/MADB/blob/main/Contributing.md) for ways to get started.

Please adhere to this project's [Code of Conduct](https://github.com/KDesp73/MADB/blob/main/CODE_OF_CONDUCT.md).

## Authors

- [@KDesp73](https://github.com/KDesp73)


## License

[MIT](https://choosealicense.com/licenses/mit/)

