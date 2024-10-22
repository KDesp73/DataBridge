package kdesp73.databridge.helpers;

import java.util.HashMap;
import java.util.List;

public class ResultRow extends HashMap<String, String> {
	private List<String> columnNames; // Add a field to store column names

	public void setColumnNames(List<String> columnNames) {
		this.columnNames = columnNames;
	}

	public List<String> getColumnNames() {
		return columnNames;
	}
}
