
# Java Methods to Manage a Microsoft Access Database (.accdb)

## Tutorial

1) Import MADB in your project as a dependency (see Releases for .jar file)
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
ArrayList<String> arr = db.SELECT("Table_name", "Column_name");

//Update a value based on the condition "WHERE Id = IdValue"
db.UPDATE("Table_name", "Column_name", value, "Column_to_check", value_to_check);
```

## Dependencies

```xml
<dependency>
    <groupId>MADB</groupId>
    <artifactId>MADB</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```


## Clone

```bash
git clone https://github.com/KDesp73/Java-Database-Methods
```

## Contributing

Contributions are always welcome!

Please adhere to this project's `code of conduct`.

## Authors

- [@KDesp73](https://github.com/KDesp73)


## License

[MIT](https://choosealicense.com/licenses/mit/)

