package kdesp73.databridge.helpers;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResultProcessor {
	/**
	 * Transforms the ResultSet object to a List of ResultRow (custom). No column argments are needed.
	 * @param resultSet
	 * @return
	 */
	public List<ResultRow> toList(ResultSet resultSet) {
		throw new RuntimeException("Faulty impementation");

		// try{
		// 	if(resultSet == null || resultSet.isClosed()){
		// 		throw new RuntimeException("ResultSet is closed");
		// 	}
		// } catch (SQLException e){
		// 	e.printStackTrace();
		// }

		// List<ResultRow> resultList = new ArrayList<>();
        // try {
        //     ResultSetMetaData metaData = resultSet.getMetaData();
        //     int columnCount = metaData.getColumnCount();

        //     List<String> columnNames = new ArrayList<>();
        //     for (int i = 1; i <= columnCount; i++) {
        //         columnNames.add(metaData.getColumnName(i));
        //     }

        //     while (resultSet.next()) {
        //         ResultRow row = new ResultRow();
        //         row.setColumnNames(columnNames); // Set column names for each row
        //         for (int i = 1; i <= columnCount; i++) {
        //             String columnName = metaData.getColumnName(i);
        //             row.put(columnName, resultSet.getString(columnName));
        //         }
        //         resultList.add(row);
        //     }
        // } catch (SQLException e) {
        //     e.printStackTrace();
        //     throw new RuntimeException("Error processing ResultSet: " + e.getMessage());
        // }
        // return resultList;
    }

	/**
	 * Transforms the ResultSet object to a List of ResultRow (custom)
	 * @param resultSet
	 * @param columns
	 * @return
	 */
    public List<ResultRow> toList(ResultSet resultSet, List<String> columns) {
		throw new RuntimeException("Faulty implementation");

		// List<ResultRow> resultList = new ArrayList<>();
        // try {
        //     while (resultSet.next()) {
        //         ResultRow row = new ResultRow();
        //         for (String column : columns) {
        //             row.put(column, resultSet.getString(column));
        //         }
        //         resultList.add(row);
        //     }
        // } catch (SQLException e) {
        //     e.printStackTrace();
        //     throw new RuntimeException("Error processing ResultSet: " + e.getMessage());
        // }
        // return resultList;
    }

	/**
	 * Prints the table based on the transformed ResultSet while also specifying which columns to print.
	 * @param resultList
	 * @param columns
	 */
	public void printTable(List<ResultRow> resultList, List<String> columns) {
        if (resultList.isEmpty()) {
            System.out.println("No data found.");
            return;
        }

        int[] columnWidths = calculateColumnWidths(resultList, columns);

        // Calculate total table width
        int totalWidth = 1 + 2 * columnWidths.length; // 1 for the "|" and 2 for spaces between columns
        for (int width : columnWidths) {
            totalWidth += width * 1.4;
        }

        // Print top horizontal line
        printHorizontalLine(totalWidth);

        // Print table header
        System.out.print("|");
        for (int i = 0; i < columns.size(); i++) {
            System.out.printf(" %-" + (columnWidths[i]) + "s |", columns.get(i));
        }
        System.out.println();

        // Print header-row separator
        printHorizontalLine(totalWidth);

        // Print table data
        for (ResultRow row : resultList) {
            System.out.print("|");
            for (int i = 0; i < columns.size(); i++) {
                String column = columns.get(i);
                System.out.printf(" %-" + (columnWidths[i]) + "s |", row.get(column));
            }
            System.out.println();
        }

        // Print bottom horizontal line
        printHorizontalLine(totalWidth);
    }

    // Helper method to print a horizontal line with the specified width
    private void printHorizontalLine(int width) {
        for (int i = 0; i < width; i++) {
            System.out.print("-");
        }
        System.out.println();
    }


    /**
	 * Prints the table based on the transformed ResultSet
	 * @param resultList
	 */
    public void printTable(List<ResultRow> resultList) {
        if (resultList.isEmpty()) {
            System.out.println("No data found.");
            return;
        }

        List<String> columns = resultList.get(0).getColumnNames();

        int[] columnWidths = calculateColumnWidths(resultList, columns);
        printTable(resultList, columns, columnWidths);
    }

    // Helper method to calculate column widths
    private int[] calculateColumnWidths(List<ResultRow> resultList, List<String> columns) {
        int[] columnWidths = new int[columns.size()];

        for (ResultRow row : resultList) {
            for (int i = 0; i < columns.size(); i++) {
                String column = columns.get(i);
                int length = row.get(column).length();
                if (length > columnWidths[i]) {
                    columnWidths[i] = length;
                }
            }
        }

        return columnWidths;
    }

    // Helper method to print the table with dynamic column widths
    private void printTable(List<ResultRow> resultList, List<String> columns, int[] columnWidths) {
        // Print table header

		// Calculate total table width
        int totalWidth = 1 + 2 * columnWidths.length; // 1 for the "|" and 2 for spaces between columns
        for (int width : columnWidths) {
            totalWidth += width * 1.4;
        }

        // Print top horizontal line
        printHorizontalLine(totalWidth);

		for (int i = 0; i < columns.size(); i++) {
            System.out.printf("| %-" + (columnWidths[i] + 2) + "s", columns.get(i));
        }
        System.out.println("|");

		printHorizontalLine(totalWidth);

        // Print table data
        for (ResultRow row : resultList) {
            for (int i = 0; i < columns.size(); i++) {
                String column = columns.get(i);
                System.out.printf("| %-" + (columnWidths[i] + 2) + "s", row.get(column));
            }
            System.out.println("|");
        }

        printHorizontalLine(totalWidth);
    }
}
