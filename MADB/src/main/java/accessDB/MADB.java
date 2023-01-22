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


package accessDB;

import java.sql.*;
import java.util.ArrayList;

/**
 * SQL Methods for managing Microsoft Access
 * Databases (.accdb) with Java
 * 
 * @author KDesp73
 * @version 1.0.2
 *
 */
public class MADB {

        private String filepath;
        private java.sql.Connection conn;
        private Statement statement;
        
        /**
         * Constructor
         * 
         * @param filepath Database file directory including the file
         * @throws SQLException 
         */
        public MADB(String filepath) throws SQLException {
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
         * Inserts a specific String in a column
         * of a table
         *
         * @param Table Table to apply changes
         * @param Column Column to insert the
         * value
         * @param Value String to be inserted
         * @throws SQLException
         *
         */
        public void INSERT(String Table, String Column, String Value) throws SQLException { //Inserts value to specific table and field
                this.getStatement().executeUpdate("INSERT INTO " + Table + " (" + Column + ") VALUES (\'" + Value + "\')");
        }

        /**
         * Inserts a specific Integer in a column
         * of a table
         *
         * @param Table Table to apply changes
         * @param Column Column to insert the
         * value
         * @param Value Integer to be inserted
         * @throws SQLException
         *
         */
        public void INSERT(String Table, String Column, int Value) throws SQLException { //Inserts value to specific table and field
                this.getStatement().executeUpdate("INSERT INTO " + Table + "(" + Column + ") VALUES(" + Value + ")");
        }

        /**
         * Inserts a specific Float in a column of
         * a table
         *
         * @param Table Table to apply changes
         * @param Column Column to insert the
         * value
         * @param Value Float to be inserted
         * @throws SQLException
         *
         */
        public void INSERT(String Table, String Column, float Value) throws SQLException { //Inserts value to specific table and field
                this.getStatement().executeUpdate("INSERT INTO " + Table + "(" + Column + ") VALUES(" + Value + ")");
        }

        /**
         * Inserts a specific Double in a column
         * of a table
         *
         * @param Table Table to apply changes
         * @param Column Column to insert the
         * value
         * @param Value Double to be inserted
         * @throws SQLException
         *
         */
        public void INSERT(String Table, String Column, double Value) throws SQLException { //Inserts value to specific table and field
                this.getStatement().executeUpdate("INSERT INTO " + Table + "(" + Column + ") VALUES(" + Value + ")");
        }

        /**
         * Inserts a specific Boolean in a column
         * of a table
         *
         * @param Table Table to apply changes
         * @param Column Column to insert the
         * value
         * @param Value Boolean to be inserted
         * @throws SQLException
         *
         */
        public void INSERT(String Table, String Column, boolean Value) throws SQLException { //Inserts value to specific table and field
                this.getStatement().executeUpdate("INSERT INTO " + Table + "(" + Column + ") VALUES(" + Value + ")");
        }

        /**
         *
         * Inserts multiple Strings in multiple
         * columns
         *
         * @param Table Table to apply changes
         * @param Columns Columns to insert the
         * Strings in their respective order
         * @param Values Strings to be inserted in
         * Array format
         * @throws SQLException
         */
        public void INSERT(String Table, String[] Columns, String[] Values) throws SQLException { //Inserts value to specific table and field
                this.getStatement().executeUpdate("INSERT INTO " + Table + "(" + Utils.arrayToList(Columns) + ") VALUES(" + Utils.stringToList(Values) + ")");
        }

        /**
         *
         * Inserts multiple Integers in multiple
         * columns
         *
         * @param Table Table to apply changes
         * @param Columns Columns to insert the
         * Integers in their respective order
         * @param Values Integers to be inserted
         * in Array format
         * @throws SQLException
         */
        public void INSERT(String Table, String[] Columns, int[] Values) throws SQLException { //Inserts value to specific table and field
                this.getStatement().executeUpdate("INSERT INTO " + Table + "(" + Utils.arrayToList(Columns) + ") VALUES(" + Utils.arrayToList(Values) + ")");
        }

        /**
         *
         * Inserts multiple Floats in multiple
         * columns
         *
         * @param Table Table to apply changes
         * @param Columns Columns to insert the
         * Floats in their respective order
         * @param Values Floats to be inserted in
         * Array format
         * @throws SQLException
         */
        public void INSERT(String Table, String[] Columns, float[] Values) throws SQLException { //Inserts value to specific table and field
                this.getStatement().executeUpdate("INSERT INTO " + Table + "(" + Utils.arrayToList(Columns) + ") VALUES(" + Utils.arrayToList(Values) + ")");
        }

        /**
         *
         * Inserts multiple Doubles in multiple
         * columns
         *
         * @param Table Table to apply changes
         * @param Columns Columns to insert the
         * Doubles in their respective order
         * @param Values Doubles to be inserted in
         * Array format
         * @throws SQLException
         */
        public void INSERT(String Table, String[] Columns, double[] Values) throws SQLException { //Inserts value to specific table and field
                this.getStatement().executeUpdate("INSERT INTO " + Table + "(" + Utils.arrayToList(Columns) + ") VALUES(" + Utils.arrayToList(Values) + ")");
        }

        /**
         *
         * Inserts multiple Booleans in multiple
         * columns
         *
         * @param Table Table to apply changes
         * @param Columns Columns to insert the
         * Booleans in their respective order
         * @param Values Booleans to be inserted
         * in Array format
         * @throws SQLException
         */
        public void INSERT(String Table, String[] Columns, boolean[] Values) throws SQLException { //Inserts value to specific table and field
                this.getStatement().executeUpdate("INSERT INTO " + Table + "(" + Utils.arrayToList(Columns) + ") VALUES(" + Utils.arrayToList(Values) + ")");
        }

        /**
         *
         * Inserts multiple Strings in multiple
         * columns
         *
         * @param Table Table to apply changes
         * @param Columns Columns to insert the
         * Strings in their respective order
         * @param Values Strings to be inserted in
         * ArrayList format
         * @throws SQLException
         */
        public void INSERT(String Table, String[] Columns, ArrayList<String> Values) throws SQLException { //Inserts value to specific table and field
                this.getStatement().executeUpdate("INSERT INTO " + Table + "(" + Utils.arrayToList(Columns) + ") VALUES(" + Utils.stringToList(Values) + ")");
        }

        /*===========================================================*/
        /**
         * Updates a specific cell of the Table when the condition "WHERE Id = IdValue" is True
         * 
         * @param Table Table to apply changes
         * @param Column Column where the change will happen
         * @param Value String to replace existing value
         * @param Id Column to check for the condition
         * @param IdValue String to check for the condition
         * @throws SQLException
         */
        public void UPDATE(String Table, String Column, String Value, String Id, String IdValue) throws SQLException {
                this.getStatement().executeUpdate("UPDATE " + Table + " SET " + Column + " = \'" + Value + "\' WHERE " + Id + " = \'" + IdValue + "\'");
        }
        
        /**
         * Updates a specific cell of the Table when the condition "WHERE Id = IdValue" is True
         * 
         * @param Table Table to apply changes
         * @param Column Column where the change will happen
         * @param Value String to replace existing value
         * @param Id Column to check for the condition
         * @param IdValue Integer to check for the condition
         * @throws SQLException
         */
        public void UPDATE(String Table, String Column, String Value, String Id, int IdValue) throws SQLException {
                this.getStatement().executeUpdate("UPDATE " + Table + " SET " + Column + " = \'" + Value + "\' WHERE " + Id + " = " + IdValue);
        }
        
        /**
         * Updates a specific cell of the Table when the condition "WHERE Id = IdValue" is True
         * 
         * @param Table Table to apply changes
         * @param Column Column where the change will happen
         * @param Value Integer to replace existing value
         * @param Id Column to check for the condition
         * @param IdValue String to check for the condition
         * @throws SQLException
         */
        public void UPDATE(String Table, String Column, int Value, String Id, String IdValue) throws SQLException {
                this.getStatement().executeUpdate("UPDATE " + Table + " SET " + Column + " = \'" + Value + "\' WHERE " + Id + " = \'" + IdValue + "\'");
        }
        
        /**
         * Updates a specific cell of the Table when the condition "WHERE Id = IdValue" is True
         * 
         * @param Table Table to apply changes
         * @param Column Column where the change will happen
         * @param Value Integer to replace existing value
         * @param Id Column to check for the condition
         * @param IdValue Integer to check for the condition
         * @throws SQLException
         */
        public void UPDATE(String Table, String Column, int Value, String Id, int IdValue) throws SQLException {
                this.getStatement().executeUpdate("UPDATE " + Table + " SET " + Column + " = \'" + Value + "\' WHERE " + Id + " = " + IdValue);
        }
        
        /**
         * Updates a specific cell of the Table when the condition "WHERE Id = IdValue" is True
         * 
         * @param Table Table to apply changes
         * @param Column Column where the change will happen
         * @param Value Double to replace existing value
         * @param Id Column to check for the condition
         * @param IdValue String to check for the condition
         * @throws SQLException
         */
        public void UPDATE(String Table, String Column, double Value, String Id, String IdValue) throws SQLException {
                this.getStatement().executeUpdate("UPDATE " + Table + " SET " + Column + " = \'" + Value + "\' WHERE " + Id + " = \'" + IdValue + "\'");
        }
        
        /**
         * Updates a specific cell of the Table when the condition "WHERE Id = IdValue" is True
         * 
         * @param Table Table to apply changes
         * @param Column Column where the change will happen
         * @param Value Double to replace existing value
         * @param Id Column to check for the condition
         * @param IdValue Integer to check for the condition
         * @throws SQLException
         */
        public void UPDATE(String Table, String Column, double Value, String Id, int IdValue) throws SQLException {
                this.getStatement().executeUpdate("UPDATE " + Table + " SET " + Column + " = \'" + Value + "\' WHERE " + Id + " = " + IdValue);
        }

        /**
         * Updates a specific cell of the Table when the condition "WHERE Id = IdValue" is True
         * 
         * @param Table Table to apply changes
         * @param Column Column where the change will happen
         * @param Value Boolean to replace existing value
         * @param Id Column to check for the condition
         * @param IdValue String to check for the condition
         * @throws SQLException
         */
        public void UPDATE(String Table, String Column, boolean Value, String Id, String IdValue) throws SQLException {
                this.getStatement().executeUpdate("UPDATE " + Table + " SET " + Column + " = \'" + Value + "\' WHERE " + Id + " = \'" + IdValue + "\'");
        }
        
        /**
         * Updates a specific cell of the Table when the condition "WHERE Id = IdValue" is True
         * 
         * @param Table Table to apply changes
         * @param Column Column where the change will happen
         * @param Value Boolean to replace existing value
         * @param Id Column to check for the condition
         * @param IdValue Integer to check for the condition
         * @throws SQLException
         */
        public void UPDATE(String Table, String Column, boolean Value, String Id, int IdValue) throws SQLException {
                this.getStatement().executeUpdate("UPDATE " + Table + " SET " + Column + " = \'" + Value + "\' WHERE " + Id + " = " + IdValue);
        }
        
        /**
         * Updates every cell of a Column with no condition
         * 
         * @param Table Table to apply changes
         * @param Column Column where the change will happen
         * @param Value String to replace existing values
         * @throws SQLException 
         */
        public void UPDATE(String Table, String Column, String Value) throws SQLException {
                this.getStatement().executeUpdate("UPDATE " + Table + " SET " + Column + " = \'" + Value + "\'");
        }

        /**
         * Updates every cell of a Column with no condition
         * 
         * @param Table Table to apply changes
         * @param Column Column where the change will happen
         * @param Value Integer to replace existing values
         * @throws SQLException 
         */
        public void UPDATE(String Table, String Column, int Value) throws SQLException {
                this.getStatement().executeUpdate("UPDATE " + Table + " SET " + Column + " = " + Value);
        }

        /**
         * Updates every cell of a Column with no condition
         * 
         * @param Table Table to apply changes
         * @param Column Column where the change will happen
         * @param Value Double to replace existing values
         * @throws SQLException 
         */
        public void UPDATE(String Table, String Column, double Value) throws SQLException {
                this.getStatement().executeUpdate("UPDATE " + Table + " SET " + Column + " = " + Value);
        }
        
        /**
         * Updates every cell of a Column with no condition
         * 
         * @param Table Table to apply changes
         * @param Column Column where the change will happen
         * @param Value Boolean to replace existing values
         * @throws SQLException 
         */
        public void UPDATE(String Table, String Column, boolean Value) throws SQLException {
                this.getStatement().executeUpdate("UPDATE " + Table + " SET " + Column + " = " + Value);
        }

        /*===========================================================*/
        
        /**
         * Deletes records when the condition "WHERE Column = Value" is True if the Value exists
         * 
         * @param Table Table to apply changes
         * @param Column Column to check for the condition
         * @param Value String to check for the condition
         * @throws SQLException 
         */
        public void DELETE(String Table, String Column, String Value) throws SQLException { //Deletes record
                if (!valueExists(Table, Column, Value)) {
                        System.out.println("Record doesn't exist");
                        return;
                }

                String query = "DELETE FROM " + Table + " WHERE " + Column + " = \'" + Value + "\'";

                this.getStatement().executeUpdate(query);
                System.out.println("Record deleted");
        }
        
        /**
         * Deletes records when the condition "WHERE Column = Value" is True if the Value exists
         * 
         * @param Table Table to apply changes
         * @param Column Column to check for the condition
         * @param Value Integer to check for the condition
         * @throws SQLException 
         */
        public void DELETE(String Table, String Column, int Value) throws SQLException { //Deletes record
                if (!valueExists(Table, Column, Value)) {
                        System.out.println("Record doesn't exist");
                        return;
                }

                String query = "DELETE FROM " + Table + " WHERE " + Column + " = " + Value;

                this.getStatement().executeUpdate(query);
                System.out.println("Record deleted");
        }
        
        /**
         * Deletes records when the condition "WHERE Column = Value" is True if the Value exists
         * 
         * @param Table Table to apply changes
         * @param Column Column to check for the condition
         * @param Value Double to check for the condition
         * @throws SQLException 
         */
        public void DELETE(String Table, String Column, double Value) throws SQLException { //Deletes record
                if (!valueExists(Table, Column, Value)) {
                        System.out.println("Record doesn't exist");
                        return;
                }

                String query = "DELETE FROM " + Table + " WHERE " + Column + " = " + Value;

                this.getStatement().executeUpdate(query);
                System.out.println("Record deleted");
        }
        
        /**
         * Deletes records when the condition "WHERE Column = Value" is True if the Value exists
         * 
         * @param Table Table to apply changes
         * @param Column Column to check for the condition
         * @param Value Boolean to check for the condition
         * @throws SQLException 
         */
        public void DELETE(String Table, String Column, boolean Value) throws SQLException { //Deletes record
                if (!valueExists(Table, Column, Value)) {
                        System.out.println("Record doesn't exist");
                        return;
                }

                String query = "DELETE FROM " + Table + " WHERE " + Column + " = " + Value;

                this.getStatement().executeUpdate(query);
                System.out.println("Record deleted");
        }
        
        /**
         * Clears the whole Table
         * 
         * @param Table Table to clear
         * @throws SQLException 
         */
        public void DELETE(String Table) throws SQLException { //Clears Table
                String query = "DELETE FROM " + Table;
                this.getStatement().executeUpdate(query);

                System.out.println("Table Cleared");
        }

        /*===========================================================*/
        
        /**
         * Returns ArrayList of Strings when the condition "WHERE Column = Value" is True
         * 
         * @param Table Table to search
         * @param Column Column to return and search
         * @param Value String to check for the condition
         * @return ArrayList
         * @throws SQLException 
         */
        public ArrayList<String> SELECT(String Table, String Column, String Value) throws SQLException {
                ArrayList<String> list = new ArrayList<>();

                String query = "SELECT " + Column + " FROM " + Table + " WHERE " + Column + "= \'" + Value + "\'";
                ResultSet rs = this.getStatement().executeQuery(query);

                while (rs.next()) {

                        list.add(rs.getString(1));

                }

                return list;
        }
        
        /**
         * Returns ArrayList of Integers when the condition "WHERE Column = Value" is True
         * 
         * @param Table Table to search
         * @param Column Column to return and search
         * @param Value Integer to check for the condition
         * @return ArrayList
         * @throws SQLException 
         */
        public ArrayList<Integer> SELECT(String Table, String Column, int Value) throws SQLException {
                ArrayList<Integer> list = new ArrayList<>();

                String query = "SELECT " + Column + " FROM " + Table + " WHERE " + Column + "= '" + Value;
                ResultSet rs = this.getStatement().executeQuery(query);

                while (rs.next()) {

                        list.add(rs.getInt(1));

                }

                return list;
        }
        
        /**
         * Returns ArrayList of Doubles when the condition "WHERE Column = Value" is True
         * 
         * @param Table Table to search
         * @param Column Column to return and search
         * @param Value Double to check for the condition
         * @return ArrayList
         * @throws SQLException 
         */
        public ArrayList<Double> SELECT(String Table, String Column, double Value) throws SQLException {
                ArrayList<Double> list = new ArrayList<>();

                String query = "SELECT " + Column + " FROM " + Table + " WHERE " + Column + "= '" + Value;
                ResultSet rs = this.getStatement().executeQuery(query);

                while (rs.next()) {

                        list.add(rs.getDouble(1));

                }

                return list;
        }
        
        /**
         * Returns ArrayList of Booleans when the condition "WHERE Column = Value" is True
         * 
         * @param Table Table to search
         * @param Column Column to return and search
         * @param Value Boolean to check for the condition
         * @return ArrayList
         * @throws SQLException 
         */
        public ArrayList<Boolean> SELECT(String Table, String Column, boolean Value) throws SQLException {
                ArrayList<Boolean> list = new ArrayList<>();

                String query = "SELECT " + Column + " FROM " + Table + " WHERE " + Column + "= '" + Value;
                ResultSet rs = this.getStatement().executeQuery(query);

                while (rs.next()) {

                        list.add(rs.getBoolean(1));

                }

                return list;
        }

        /**
         * Returns ArrayList of Floats when the condition "WHERE Column = Value" is True
         * 
         * @param Table Table to search
         * @param Column Column to return and search
         * @param Value Float to check for the condition
         * @return ArrayList
         * @throws SQLException 
         */
        public ArrayList<Float> SELECT(String Table, String Column, float Value) throws SQLException {
                ArrayList<Float> list = new ArrayList<>();

                String query = "SELECT " + Column + " FROM " + Table + " WHERE " + Column + "= '" + Value;
                ResultSet rs = this.getStatement().executeQuery(query);

                while (rs.next()) {

                        list.add(rs.getFloat(1));

                }

                return list;
        }
        
        /**
         * Returns the whole Column
         * 
         * @param Table Table to search
         * @param Column Column to return and search
         * @return ArrayList
         * @throws SQLException 
         */
        public ArrayList<String> SELECT(String Table, String Column) throws SQLException {
                ArrayList<String> list = new ArrayList<>();

                String query = "SELECT " + Column + " FROM " + Table;
                ResultSet rs = this.getStatement().executeQuery(query);

                while (rs.next()) {

                        list.add(rs.getString(1));

                }

                return list;
        }

        /**
         * Returns ArrayList of String[]
         * 
         * @param Table Table to search
         * @param Columns Array of columns to return and search
         * @return ArrayList
         * @throws SQLException 
         */
        public ArrayList<String[]> SELECT(String Table, String[] Columns) throws SQLException {
                ArrayList<String[]> list = new ArrayList<>();

                String query = "SELECT " + Utils.arrayToList(Columns) + " FROM " + Table;
                ResultSet rs = this.getStatement().executeQuery(query);

                while (rs.next()) {
                        String[] str = new String[Columns.length];

                        for (int i = 0; i < Columns.length; i++) {
                                str[i] = rs.getString(i + 1);
                        }

                        list.add(str);
                }

                return list;
        }
        
        /**
         * Returns ArrayList of String[] when the condition "WHERE Column = Value" is True
         * 
         * @param Table Table to search
         * @param Columns Array of columns to return and search
         * @param Column Column to check for the condition
         * @param Value String to check for the condition
         * @return ArrayList
         * @throws SQLException 
         */
        public ArrayList<String[]> SELECT(String Table, String[] Columns, String Column, String Value) throws SQLException {
                ArrayList<String[]> list = new ArrayList<>();

                String query = "SELECT " + Utils.arrayToList(Columns) + " FROM " + Table + " WHERE " + Column + " = \'" + Value + "\'";
                ResultSet rs = this.getStatement().executeQuery(query);

                while (rs.next()) {
                        String[] str = new String[Columns.length];

                        for (int i = 0; i < Columns.length; i++) {
                                str[i] = rs.getString(i + 1);
                        }

                        list.add(str);
                }

                return list;
        }

        /*===========================================================*/
        
        /**
         * Searches for the existence of a specific String
         * 
         * @param Table Table to search for Value
         * @param Column Column to search for Value
         * @param Value String to search
         * @return Boolean
         * @throws SQLException 
         */
        public boolean valueExists(String Table, String Column, String Value) throws SQLException {
                String query = "SELECT " + Column + " FROM " + Table + " WHERE " + Column + "=\'" + Value + "\'";

                ResultSet rs = this.getStatement().executeQuery(query);
                if (rs.next()) {
                        return true;
                }

                return (false);
        }

        /**
         * Searches for the existence of a specific Integer
         * 
         * @param Table Table to search for Value
         * @param Column Column to search for Value
         * @param Value Integer to search
         * @return Boolean
         * @throws SQLException 
         */
        public boolean valueExists(String Table, String Column, int Value) throws SQLException {
                String query = "SELECT " + Column + " FROM " + Table + " WHERE " + Column + "=" + Value;

                ResultSet rs = this.getStatement().executeQuery(query);
                if (rs.next()) {
                        return true;
                }

                return (false);
        }
        
        /**
         * Searches for the existence of a specific Double
         * 
         * @param Table Table to search for Value
         * @param Column Column to search for Value
         * @param Value Double to search
         * @return Boolean
         * @throws SQLException 
         */
        public boolean valueExists(String Table, String Column, double Value) throws SQLException {
                String query = "SELECT " + Column + " FROM " + Table + " WHERE " + Column + "=" + Value;

                ResultSet rs = this.getStatement().executeQuery(query);
                if (rs.next()) {
                        return true;
                }

                return (false);
        }
        
        /**
         * Searches for the existence of a specific Boolean
         * 
         * @param Table Table to search for Value
         * @param Column Column to search for Value
         * @param Value Boolean to search
         * @return Boolean
         * @throws SQLException 
         */
        public boolean valueExists(String Table, String Column, boolean Value) throws SQLException {
                String query = "SELECT " + Column + " FROM " + Table + " WHERE " + Column + "=" + Value;

                ResultSet rs = this.getStatement().executeQuery(query);
                if (rs.next()) {
                        return true;
                }

                return (false);
        }

        /*===========================================================*/
        
        /**
         * Returns the number of records that exist in a Table
         * 
         * @param Table Table to count records from
         * @return Integer
         * @throws SQLException 
         */
        public int numOfRecords(String Table) throws SQLException {
                ResultSet rs = this.getStatement().executeQuery("SELECT COUNT(*)  FROM " + Table);

                rs.next();
                return rs.getInt(1);
        }
        
        /**
         * Returns the number of records that exist in a Column of a Table
         * 
         * @param Table Table to count records from
         * @param Column Column to count records from
         * @return Integer
         * @throws SQLException 
         */
        public int numOfRecords(String Table, String Column) throws SQLException {
                ResultSet rs = this.getStatement().executeQuery("SELECT COUNT( " + Column + ")  FROM " + Table);

                rs.next();
                return rs.getInt(1);
        }

        /*===========DataBase Utils===========*/
        class Utils {

                private static String arrayToList(int[] arr) {
                        String s = "";

                        for (int i = 0; i < arr.length; i++) {
                                if (i == arr.length - 1) {
                                        s = s.concat("" + arr[i]);
                                } else {
                                        s = s.concat(arr[i] + ", ");
                                }
                        }

                        return s;
                }

                private static String arrayToList(float[] arr) {
                        String s = "";

                        for (int i = 0; i < arr.length; i++) {
                                if (i == arr.length - 1) {
                                        s = s.concat("" + arr[i]);
                                } else {
                                        s = s.concat(arr[i] + ", ");
                                }
                        }

                        return s;
                }

                private static String arrayToList(double[] arr) {
                        String s = "";

                        for (int i = 0; i < arr.length; i++) {
                                if (i == arr.length - 1) {
                                        s = s.concat("" + arr[i]);
                                } else {
                                        s = s.concat(arr[i] + ", ");
                                }
                        }

                        return s;
                }

                private static String arrayToList(boolean[] arr) {
                        String s = "";

                        for (int i = 0; i < arr.length; i++) {
                                if (i == arr.length - 1) {
                                        s = s.concat("" + arr[i]);
                                } else {
                                        s = s.concat(arr[i] + ", ");
                                }
                        }

                        return s;
                }

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

                private static String stringToList(String[] arr) {
                        String s = "";

                        for (int i = 0; i < arr.length; i++) {
                                if (i == arr.length - 1) {
                                        s = s.concat("\'" + arr[i] + "\'");
                                } else {
                                        s = s.concat("\'" + arr[i] + "\', ");
                                }
                        }

                        return s;
                }

                private static String stringToList(ArrayList<String> arr) {
                        String s = "";

                        for (int i = 0; i < arr.size(); i++) {
                                if (i == arr.size() - 1) {
                                        s = s.concat("\'" + arr.get(i) + "\'");
                                } else {
                                        s = s.concat("\'" + arr.get(i) + "\', ");
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

                private static String tableColsFormat(ArrayList<String> cols, ArrayList<String> types) {
                        String s = "";
                        for (int i = 0; i < cols.size(); i++) {
                                if (i == cols.size() - 1) {
                                        s = s.concat(cols.get(i) + " " + types.get(i));
                                } else {
                                        s = s.concat(cols.get(i) + " " + types.get(i) + ", ");
                                }

                        }
                        return s;
                }
        }
}
