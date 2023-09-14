package kdesp73.madb;

import java.sql.SQLException;
import java.util.ArrayList;

public interface SQLInterface {

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
    void INSERT(String Table, String Column, Object Value) throws SQLException;

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
    void INSERT(String Table, String[] Columns, ArrayList<Object> Values) throws SQLException;

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
    void INSERT(String Table, String[] Columns, Object[] Values) throws SQLException;

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
    void UPDATE(String Table, String Column, Object Value, Condition c) throws SQLException;

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
    void UPDATE(String Table, String Column, Object Value) throws SQLException;

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
    void DELETE(String Table, String Column, Object Value) throws SQLException, ValueNotFoundException;

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
    void DELETE(String Table, String Column, Condition c) throws SQLException;

    /**
     * Clears the whole Table (!)
     *
     * @param Table Table to clear
     * @throws SQLException
     */
    void DELETE(String Table) throws SQLException;

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
    Object SELECT(String Table, String Column, Condition c) throws SQLException;

    /**
     * Returns the whole Column
     *
     * @param Table Table to search
     * @param Column Column to return and
     * search
     * @return ArrayList
     * @throws SQLException
     */
    ArrayList<Object> SELECT(String Table, String Column) throws SQLException;

    /**
     * Returns ArrayList of String[]
     *
     * @param Table Table to search
     * @param Columns Array of columns to
     * return and search
     * @return ArrayList
     * @throws SQLException
     */
    ArrayList<Object[]> SELECT(String Table, String[] Columns) throws SQLException;

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
    ArrayList<Object[]> SELECT(String Table, String[] Columns, Condition c) throws SQLException;

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
    boolean valueExists(String Table, String Column, Object Value) throws SQLException;

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
    int numOfRecords(String Table) throws SQLException;

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
    int numOfRecords(String Table, String Column) throws SQLException;

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
    int numOfRecords(String Table, String Column, Condition c) throws SQLException;

}