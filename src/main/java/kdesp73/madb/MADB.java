/*
 * MIT License
 *
 * Copyright (c) 2023 Konstantinos Despoinidis

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package kdesp73.madb;

import java.sql.*;
import java.util.ArrayList;

import kdesp73.madb.exceptions.ValueNotFoundException;

import java.sql.DriverManager;

/**
 * SQL Methods for managing Microsoft Access
 * Databases (.accdb) with Java
 *
 * @author KDesp73
 * @version 1.0.7
 *
 */
public class MADB implements SQLInterface {

    private String filepath;
    private java.sql.Connection conn;
    private Statement statement;

    /**
     * Constructor
     *
     * @param filepath Database file directory
     * including the file
     * @throws SQLException
     */
    public MADB(String filepath) throws SQLException {
        try{
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
        } catch(ClassNotFoundException e) {
        }

        String url = "jdbc:ucanaccess://" + filepath;
        this.filepath = filepath;
        conn = DriverManager.getConnection(url);
        statement = conn.createStatement();
    }

    public String getPath() {
        return filepath;
    }

    public Statement getStatement() {
        return statement;
    }

    /**
     * Inserts a specific Value in a column of
     * a table
     *
     * @param Table Table to apply changes
     * @param Column Column to insert the
     * value
     * @param Value Value to be inserted
     * @throws SQLException
     *
     */
    @Override
    public void INSERT(String Table, String Column, Object Value) throws SQLException { //Inserts value to specific table and field
        String query;
        if (Value instanceof String) {
            query = "INSERT INTO " + Table + " (" + Column + ") VALUES (\'" + Value + "\')";
        } else {
            query = "INSERT INTO " + Table + " (" + Column + ") VALUES (" + Value + ")";
        }

        this.getStatement().executeUpdate(query);
    }

    /**
     *
     * Inserts multiple Strings in multiple
     * columns
     *
     * @param Table Table to apply changes
     * @param Columns Columns to insert the
     * Strings in their respective order
     * @param Values Values to be inserted in
     * ArrayList format
     * @throws SQLException
     */
    @Override
    public void INSERT(String Table, String[] Columns, ArrayList<Object> Values) throws SQLException { //Inserts value to specific table and field
        this.getStatement().executeUpdate("INSERT INTO " + Table + "(" + Utils.arrayToList(Columns) + ") VALUES(" + Utils.objectToList(Values) + ")");
    }

    /**
     * Inserts multiple Strings in multiple
     * columns
     *
     * @param Table Table to apply changes
     * @param Columns Columns to insert the
     * Strings in their respective order
     * @param Values Values to be inserted
     * @throws SQLException
     */
    @Override
    public void INSERT(String Table, String[] Columns, Object[] Values) throws SQLException {
        String query = "INSERT INTO " + Table + "(" + Utils.arrayToList(Columns) + ") VALUES(" + Utils.objectToList(Values) + ")";

        this.getStatement().executeUpdate(query);
    }

    /*===========================================================*/
    /**
     * Updates a specific cell of the Table
     * when the condition is True
     *
     * @param Table Table to apply changes
     * @param Column Column where the change
     * will happen
     * @param Value New value to replace
     * existing one
     * @param c Custom Condition
     * @throws SQLException
     */
    @Override
    public void UPDATE(String Table, String Column, Object Value, Condition c) throws SQLException {
        String query;
        if (Value instanceof String) {
            query = "UPDATE " + Table + " SET " + Column + " = \'" + Value + "\' WHERE " + c.getCondition();
        } else {
            query = "UPDATE " + Table + " SET " + Column + " = " + Value + " WHERE " + c.getCondition();
        }

        this.getStatement().executeUpdate(query);
    }

    /**
     * Updates every cell of a Column with no
     * condition
     *
     * @param Table Table to apply changes
     * @param Column Column where the change
     * will happen
     * @param Value New value to replace
     * existing one
     * @throws SQLException
     */
    @Override
    public void UPDATE(String Table, String Column, Object Value) throws SQLException {
        String query;
        if (Value instanceof String) {
            query = "UPDATE " + Table + " SET " + Column + " = \'" + Value + "\'";
        } else {
            query = "UPDATE " + Table + " SET " + Column + " = " + Value;
        }

        this.getStatement().executeUpdate(query);
    }

    /*===========================================================*/
    /**
     * Deletes records when the condition
     * "WHERE Column = Value" is True if the
     * Value exists
     *
     * @param Table Table to apply changes
     * @param Column Column to check for the
     * condition
     * @param Value String to check for the
     * condition
     * @throws SQLException
     */
    @Override
    public void DELETE(String Table, String Column, Object Value) throws SQLException,  ValueNotFoundException{ //Deletes record
        if (!valueExists(Table, Column, Value)) {
            return;
            //throw new ValueNotFoundException("Value not found");
        }

        String query;
        if (Value instanceof String) {
            query = "DELETE FROM " + Table + " WHERE " + Column + " = \'" + Value + "\'";
        } else {
            query = "DELETE FROM " + Table + " WHERE " + Column + " = " + Value;
        }

        this.getStatement().executeUpdate(query);
        System.out.println("Record deleted");
    }

    /**
     * Deletes records when the custom
     * condition is True
     *
     * @param Table Table to apply changes
     * @param Column Column to check for the
     * condition
     * @param c Custom Condition
     * @throws SQLException
     */
    @Override
    public void DELETE(String Table, String Column, Condition c) throws SQLException { //Deletes record
        String query = "DELETE FROM " + Table + " WHERE " + c.getCondition();

        this.getStatement().executeUpdate(query);
        System.out.println("Record deleted");
    }

    /**
     * Clears the whole Table (!)
     *
     * @param Table Table to clear
     * @throws SQLException
     */
    @Override
    public void DELETE(String Table) throws SQLException { //Clears Table
        String query = "DELETE FROM " + Table;
        this.getStatement().executeUpdate(query);

        System.out.println("Table Cleared");
    }

    /*===========================================================*/
    /**
     * Returns an Object when the custom
     * condition is True
     *
     * @param Table Table to search
     * @param Column Column to return and
     * search
     * @param c Custom Condition
     * @return ArrayList
     * @throws SQLException
     */
    @Override
    public Object SELECT(String Table, String Column, Condition c) throws SQLException {
        String query = "SELECT " + Column + " FROM " + Table + " WHERE " + c.getCondition();
        ResultSet rs = this.getStatement().executeQuery(query);

        try {
            rs.next();
            return rs.getObject(Column);
        } catch (net.ucanaccess.jdbc.UcanaccessSQLException e) {
            System.out.println("Element not found");
        }
        return null;
    }

    /**
     * Returns the whole Column
     *
     * @param Table Table to search
     * @param Column Column to return and
     * search
     * @return ArrayList
     * @throws SQLException
     */
    @Override
    public ArrayList<Object> SELECT(String Table, String Column) throws SQLException {
        ArrayList<Object> list = new ArrayList<>();

        String query = "SELECT " + Column + " FROM " + Table;
        ResultSet rs = this.getStatement().executeQuery(query);

        while (rs.next()) {
            list.add(rs.getObject(Column));
        }

        return list;
    }

    /**
     * Returns ArrayList of String[]
     *
     * @param Table Table to search
     * @param Columns Array of columns to
     * return and search
     * @return ArrayList
     * @throws SQLException
     */
    @Override
    public ArrayList<Object[]> SELECT(String Table, String[] Columns) throws SQLException {
        ArrayList<Object[]> list = new ArrayList<>();

        String query = "SELECT " + Utils.arrayToList(Columns) + " FROM " + Table;
        ResultSet rs = this.getStatement().executeQuery(query);

        while (rs.next()) {
            Object[] str = new Object[Columns.length];

            for (int i = 0; i < Columns.length; i++) {
                str[i] = rs.getObject(i + 1);
            }

            list.add(str);
        }

        return list;
    }

    /**
     * Returns ArrayList of String[] when the
     * custom condition is True
     *
     * @param Table Table to search
     * @param Columns Array of columns to
     * return and search
     * @param c Custom condition
     * @return ArrayList
     * @throws SQLException
     */
    @Override
    public ArrayList<Object[]> SELECT(String Table, String[] Columns, Condition c) throws SQLException {
        ArrayList<Object[]> list = new ArrayList<>();

        String query = "SELECT " + Utils.arrayToList(Columns) + " FROM " + Table + " WHERE " + c.getCondition();
        ResultSet rs = this.getStatement().executeQuery(query);

        while (rs.next()) {
            Object[] str = new String[Columns.length];

            for (int i = 0; i < Columns.length; i++) {
                str[i] = rs.getObject(i + 1);
            }

            list.add(str);
        }

        return list;
    }

    /*===========================================================*/
    /**
     * Searches for the existence of a
     * specific Value
     *
     * @param Table Table to search for Value
     * @param Column Column to search for
     * Value
     * @param Value Value to search
     * @return Boolean
     * @throws SQLException
     */
    @Override
    public boolean valueExists(String Table, String Column, Object Value) throws SQLException {
        String query;
        if (Value instanceof String) {
            query = "SELECT " + Column + " FROM " + Table + " WHERE " + Column + "=\'" + Value + "\'";
        } else {
            query = "SELECT " + Column + " FROM " + Table + " WHERE " + Column + "=" + Value;
        }

        ResultSet rs = this.getStatement().executeQuery(query);
        if (rs.next()) {
            return true;
        }

        return (false);
    }

    /*===========================================================*/
    /**
     * Returns the number of records that
     * exist in a Table
     *
     * @param Table Table to count records
     * from
     * @return Integer
     * @throws SQLException
     */
    @Override
    public int numOfRecords(String Table) throws SQLException {
        ResultSet rs = this.getStatement().executeQuery("SELECT COUNT(*)  FROM " + Table);

        rs.next();
        return rs.getInt(1);
    }

    /**
     * Returns the number of records that
     * exist in a Column of a Table
     *
     * @param Table Table to count records
     * from
     * @param Column Column to count records
     * from
     * @return Integer
     * @throws SQLException
     */
    @Override
    public int numOfRecords(String Table, String Column) throws SQLException {
        ResultSet rs = this.getStatement().executeQuery("SELECT COUNT( " + Column + ")  FROM " + Table);

        rs.next();
        return rs.getInt(1);
    }

    /**
     * Returns the number of records that
     * exist in a Column of a Table based on a
     * custom condition
     *
     * @param Table Table to count records
     * from
     * @param Column Column to count records
     * from
     * @param c
     * @return Integer
     * @throws SQLException
     */
    @Override
    public int numOfRecords(String Table, String Column, Condition c) throws SQLException {
        String query = "SELECT COUNT( " + Column + ")  FROM " + Table + " WHERE " + c.getCondition();

        ResultSet rs = this.getStatement().executeQuery(query);

        rs.next();
        return rs.getInt(1);
    }

    /*===========DataBase Utils===========*/
    class Utils {

        private static String arrayToList(String[] arr) {
            String s = "";

            for (int i = 0; i < arr.length; i++) {
                if (i == arr.length - 1) {
                    s = s.concat(arr[i]);
                } else {
                    s = s.concat(arr[i] + ", ");
                }
            }

            return s;
        }

        private static String objectToList(Object[] arr) {
            String s = "";

            for (int i = 0; i < arr.length; i++) {
                if (i == arr.length - 1) {
                    if (arr[i] instanceof String) {
                        s = s.concat("\'" + arr[i] + "\'");
                    } else {
                        s = s.concat(arr[i] + "");
                    }
                } else {
                    if (arr[i] instanceof String) {
                        s = s.concat("\'" + arr[i] + "\', ");
                    } else {
                        s = s.concat(arr[i] + ", ");
                    }
                }
            }

            return s;
        }

        private static String objectToList(ArrayList<Object> arr) {
            String s = "";

            for (int i = 0; i < arr.size(); i++) {
                if (i == arr.size() - 1) {
                    if (arr.get(i) instanceof String) {
                        s = s.concat("\'" + arr.get(i) + "\'");
                    } else {
                        s = s.concat(arr.get(i) + "");
                    }
                } else {
                    if (arr.get(i) instanceof String) {
                        s = s.concat("\'" + arr.get(i) + "\', ");
                    } else {
                        s = s.concat(arr.get(i) + ", ");
                    }
                }
            }

            return s;
        }

        private static boolean correctType(String type) {
            String[] types = {
                "bigint",
                "bit",
                "decimal",
                "int",
                "money",
                "numeric",
                "smallint",
                "smallmoney",
                "tinyint",
                "float",
                "real",
                "date",
                "datetime2",
                "datetimeoffset",
                "smalldatetime",
                "datetime",
                "time",
                "char",
                "varchar",
                "text",
                "nchar",
                "nvarchar",
                "ntext",
                "binary",
                "varbinary",
                "image",
                "cursor",
                "rowversion",
                "hierarchyid",
                "uniqueidentifier",
                "sql_variant",
                "xml",
                "Spatial Geometry Types",
                "Spatial Geography Types",
                "table"
            };

            for (String type1 : types) {
                if (type.equals(type1)) {
                    return true;
                }
            }
            return false;
        }
    }
}
