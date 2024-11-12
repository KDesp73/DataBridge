# Adapter Examples

[Documentation](https://kdesp73.github.io/DataBridge-Documentation/d2/deb/classkdesp73_1_1databridge_1_1helpers_1_1Adapter.html)

```java
ResultSet rs = conn.executeQuery("SELECT * FROM person;");

List<Person> people = Adapter.load(rs, Person.class);

people.forEach(System.out::println);
```
