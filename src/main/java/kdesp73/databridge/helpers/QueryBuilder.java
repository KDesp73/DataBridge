package kdesp73.databridge.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueryBuilder {
	private StringBuilder query;
	private List<String> columns;
	private List<String> values;

	public QueryBuilder() {
		initializeQuery();
		columns = new ArrayList<>();
		values = new ArrayList<>();
	}

	private void initializeQuery() {
		query = new StringBuilder();
	}

	/**
	 * Appends the SELECT operator to the query
	 * @param columns to select
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
	 * Appends the FROM operator to the query
	 * @param table to select from
	 * @return QueryBuilder
	 */
	public QueryBuilder from(String table) {
		query.append(" FROM ").append(table);
		return this;
	}

	/**
	 * Appends the WHERE operator to the query
	 * @param condition Boolean SQL condition
	 * @return QueryBuilder
	 */
	public QueryBuilder where(String condition) {
		query.append(" WHERE ").append(condition);
		return this;
	}

	/**
	 * Appends the INSERT INTO operator to the query
	 * @param table to insert data into
	 * @return QueryBuilder
	 */
	public QueryBuilder insertInto(String table) {
		query.append("INSERT INTO ").append(table);
		return this;
	}

	/**
	 * Appends multiple columns inside parenthesis
	 * @param columns to add inside parenthesis
	 * @return QueryBuilder
	 */
	public QueryBuilder columns(String... columns) {
		this.columns.clear();
		this.columns.addAll(Arrays.asList(columns));
		query.append(" (").append(String.join(", ", columns)).append(")");
		return this;
	}

	/**
	 * Appends multiple values inside parenthesis
	 * @param values to add inside parenthesis
	 * @return QueryBuilder
	 */
	public QueryBuilder values(Object... values) {
		this.values.clear();
		for (Object value : values) {
			if(value == null) continue;
			if(value instanceof String)
				this.values.add("'" + value + "'");
			else
				this.values.add(value.toString());
		}
		query.append(" VALUES (").append(String.join(", ", this.values)).append(")");
		return this;
	}

	/**
	 * Appends the UPDATE operator to the query
	 * @param table to perform the update
	 * @return QueryBuilder
	 */
	public QueryBuilder update(String table) {
		query.append("UPDATE ").append(table);
		return this;
	}

	/**
	 * Appends the SET operator along with a column and a value
	 * @param column
	 * @param value
	 * @return QueryBuilder
	 */
	public QueryBuilder set(String column, Object value) {
		if(value instanceof String)
			query.append(" SET ").append(column).append(" = '").append(value).append("'");
		else
			query.append(" SET ").append(column).append(" = ").append(value);
		return this;
	}

	/**
	 * Appends the DELETE FROM operator
	 * @param table to delete from
	 * @return QueryBuilder
	 */
	public QueryBuilder deleteFrom(String table) {
		query.append("DELETE FROM ").append(table);
		return this;
	}

	/**
	 * Build the SQL query
	 * @return String
	 */
	public String build() {
		if (query == null) {
			throw new IllegalStateException("Incomplete query construction.");
		}

		String queryString = query.toString();
		initializeQuery();
		return queryString;
	}
}
