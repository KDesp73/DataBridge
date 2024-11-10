package kdesp73.databridge.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueryBuilder {
    private StringBuilder query;
    private List<String> columns;
    private List<String> values;

    public QueryBuilder() {
        this.query = new StringBuilder();
        this.columns = new ArrayList<>();
        this.values = new ArrayList<>();
    }

    /**
     * Appends the SELECT operator to the query.
     * @param columns Columns to select.
     * @return QueryBuilder
     */
    public QueryBuilder select(String... columns) {
        query.append("SELECT ");
        if (columns.length == 0) {
            query.append("*");
        } else {
            query.append(String.join(", ", columns));
        }
        return this;
    }

    /**
     * Appends the FROM operator to the query.
     * @param table Table to select from.
     * @return QueryBuilder
     */
    public QueryBuilder from(String table) {
        query.append(" FROM ").append(table);
        return this;
    }

    /**
     * Appends the WHERE operator to the query.
     * @param condition SQL condition.
     * @return QueryBuilder
     */
    public QueryBuilder where(String condition) {
        query.append(" WHERE ").append(condition);
        return this;
    }

    /**
     * Appends the INSERT INTO operator to the query.
     * @param table Table to insert data into.
     * @return QueryBuilder
     */
    public QueryBuilder insertInto(String table) {
        query.append("INSERT INTO ").append(table);
        return this;
    }

    /**
     * Appends multiple columns inside parentheses.
     * @param columns Columns to add.
     * @return QueryBuilder
     */
    public QueryBuilder columns(String... columns) {
        this.columns.clear();
		this.columns.addAll(Arrays.asList(columns));
        query.append(" (").append(String.join(", ", columns)).append(")");
        return this;
    }

    /**
     * Appends multiple values inside parentheses.
     * @param values Values to add.
     * @return QueryBuilder
     */
    public QueryBuilder values(Object... values) {
        this.values.clear();
        for (Object value : values) {
            if (value == null) {
                this.values.add("NULL");
            } else if (value instanceof String) {
                this.values.add("'" + value + "'");
            } else {
                this.values.add(value.toString());
            }
        }
        query.append(" VALUES (").append(String.join(", ", this.values)).append(")");
        return this;
    }

    /**
     * Appends the UPDATE operator to the query.
     * @param table Table to perform the update.
     * @return QueryBuilder
     */
    public QueryBuilder update(String table) {
        query.append("UPDATE ").append(table);
        return this;
    }

    /**
     * Appends the SET operator along with a column and a value.
     * @param column Column name.
     * @param value Value for the column.
     * @return QueryBuilder
     */
    public QueryBuilder set(String column, Object value) {
        if (value instanceof String) {
            query.append(" SET ").append(column).append(" = '").append(value).append("'");
        } else {
            query.append(" SET ").append(column).append(" = ").append(value);
        }
        return this;
    }

    /**
     * Appends the DELETE FROM operator.
     * @param table Table to delete from.
     * @return QueryBuilder
     */
    public QueryBuilder deleteFrom(String table) {
        query.append("DELETE FROM ").append(table);
        return this;
    }

    /**
     * Resets the query builder for a new query.
     */
    public void reset() {
        this.query.setLength(0);
        this.columns.clear();
        this.values.clear();
    }

    /**
     * Builds and returns the final SQL query.
     * @return The SQL query string.
     */
    public String build() {
        if (query.length() == 0) {
            throw new IllegalStateException("Incomplete query construction.");
        }

        String queryString = query.toString();
        reset();  // Reset the builder after building the query.
        return queryString;
    }
}
