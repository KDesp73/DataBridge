package io.github.kdesp73.databridge.helpers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QueryBuilderTest {

    @Test
    void select() {
        QueryBuilder qb = new QueryBuilder();
        String query = qb.select("col1", "col2").build();
        assertEquals("SELECT col1, col2", query, "SELECT query with columns failed.");
    }

    @Test
    void selectAll() {
        QueryBuilder qb = new QueryBuilder();
        String query = qb.select().build();
        assertEquals("SELECT *", query, "SELECT * query failed.");
    }

    @Test
    void from() {
        QueryBuilder qb = new QueryBuilder();
        String query = qb.select("col1").from("my_table").build();
        assertEquals("SELECT col1 FROM my_table", query, "FROM clause failed.");
    }

    @Test
    void where() {
        QueryBuilder qb = new QueryBuilder();
        String query = qb.select("*").from("my_table").where("col1 = 1").build();
        assertEquals("SELECT * FROM my_table WHERE col1 = 1", query, "WHERE clause failed.");
    }

    @Test
    void insertInto() {
        QueryBuilder qb = new QueryBuilder();
        String query = qb.insertInto("my_table").columns("col1", "col2").values("val1", 123).build();
        assertEquals("INSERT INTO my_table (col1, col2) VALUES ('val1', 123)", query, "INSERT INTO query failed.");
    }

    @Test
    void update() {
        QueryBuilder qb = new QueryBuilder();
        String query = qb.update("my_table").set("col1", "new_value").build();
        assertEquals("UPDATE my_table SET col1 = 'new_value'", query, "UPDATE query failed.");
    }

    @Test
    void updateWithNumericValue() {
        QueryBuilder qb = new QueryBuilder();
        String query = qb.update("my_table").set("col1", 42).build();
        assertEquals("UPDATE my_table SET col1 = 42", query, "UPDATE query with numeric value failed.");
    }

    @Test
    void deleteFrom() {
        QueryBuilder qb = new QueryBuilder();
        String query = qb.deleteFrom("my_table").where("col1 = 1").build();
        assertEquals("DELETE FROM my_table WHERE col1 = 1", query, "DELETE query failed.");
    }

    @Test
    void reset() {
        QueryBuilder qb = new QueryBuilder();
        qb.select("*").from("my_table");
        qb.reset();
        assertThrows(IllegalStateException.class, qb::build, "Reset did not clear the query builder.");
    }

    @Test
    void buildWithoutReset() {
        QueryBuilder qb = new QueryBuilder();
        qb.select("*").from("my_table");
        String query = qb.build();
        assertEquals("SELECT * FROM my_table", query, "Build method did not return the expected query.");
        assertThrows(IllegalStateException.class, qb::build, "QueryBuilder did not reset after build.");
    }

    @Test
    void emptyBuildThrowsException() {
        QueryBuilder qb = new QueryBuilder();
        assertThrows(IllegalStateException.class, qb::build, "Building an empty query did not throw an exception.");
    }
}
