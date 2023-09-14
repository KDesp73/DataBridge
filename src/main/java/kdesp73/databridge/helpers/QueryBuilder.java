package kdesp73.madb.helpers;

import java.util.ArrayList;
import java.util.List;

public class QueryBuilder {
    private StringBuilder query;
    private List<String> columns;
    private String table;
    private String condition;
    private List<String> values;

    public QueryBuilder() {
        columns = new ArrayList<>();
        values = new ArrayList<>();
    }

    private void initializeQuery() {
        query = new StringBuilder();
    }

    // SELECT operation
    public QueryBuilder select(String... columns) {
        initializeQuery();
        query.append("SELECT ");
        if (columns.length == 0) {
            query.append("*");
        } else {
            query.append(String.join(", ", columns));
        }
        return this;
    }

    // FROM clause
    public QueryBuilder from(String table) {
        this.table = table;
        query.append(" FROM ").append(table);
        return this;
    }

    // WHERE clause
    public QueryBuilder where(String condition) {
        this.condition = condition;
        query.append(" WHERE ").append(condition);
        return this;
    }

    // INSERT operation
    public QueryBuilder insertInto(String table) {
        initializeQuery();
        query.append("INSERT INTO ").append(table);
        return this;
    }

    public QueryBuilder columns(String... columns) {
        this.columns.clear();
        for (String column : columns) {
            this.columns.add(column);
        }
        query.append(" (").append(String.join(", ", columns)).append(")");
        return this;
    }

    public QueryBuilder values(String... values) {
        this.values.clear();
        for (String value : values) {
            this.values.add("'" + value + "'");
        }
        query.append(" VALUES (").append(String.join(", ", this.values)).append(")");
        return this;
    }

    // UPDATE operation
    public QueryBuilder update(String table) {
        initializeQuery();
        this.table = table;
        query.append("UPDATE ").append(table);
        return this;
    }

    public QueryBuilder set(String column, String value) {
        query.append(" SET ").append(column).append(" = '").append(value).append("'");
        return this;
    }

    // DELETE operation
    public QueryBuilder deleteFrom(String table) {
        initializeQuery();
        this.table = table;
        query.append("DELETE FROM ").append(table);
        return this;
    }

    // Build and get the SQL query string
    public String build() {
        if (query == null) {
            throw new IllegalStateException("Incomplete query construction.");
        }
        return query.toString();
    }
}
