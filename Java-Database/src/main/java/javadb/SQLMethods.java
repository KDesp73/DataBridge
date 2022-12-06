package javadb;

import java.sql.*;
import java.util.ArrayList;


public class SQLMethods {

        public static void INSERT(Statement s, String Table, String Column, String Value) throws SQLException { //Inserts value to specific table and field
                s.executeUpdate("INSERT INTO " + Table + " (" + Column + ") VALUES (\'" + Value + "\')");
        }

        public static void INSERT(Statement s, String Table, String Column, int Value) throws SQLException { //Inserts value to specific table and field
                s.executeUpdate("INSERT INTO " + Table + "(" + Column + ") VALUES(" + Value + ")");
        }

        public static void INSERT(Statement s, String Table, String Column, float Value) throws SQLException { //Inserts value to specific table and field
                s.executeUpdate("INSERT INTO " + Table + "(" + Column + ") VALUES(" + Value + ")");
        }

        public static void INSERT(Statement s, String Table, String Column, double Value) throws SQLException { //Inserts value to specific table and field
                s.executeUpdate("INSERT INTO " + Table + "(" + Column + ") VALUES(" + Value + ")");
        }

        public static void INSERT(Statement s, String Table, String Column, boolean Value) throws SQLException { //Inserts value to specific table and field
                s.executeUpdate("INSERT INTO " + Table + "(" + Column + ") VALUES(" + Value + ")");
        }

        public static void INSERT(Statement s, String Table, String[] Columns, String[] Values) throws SQLException { //Inserts value to specific table and field
                s.executeUpdate("INSERT INTO " + Table + "(" + Utils.arrayToList(Columns) + ") VALUES(" + Utils.stringToList(Values) + ")");
        }

        public static void INSERT(Statement s, String Table, String[] Columns, int[] Values) throws SQLException { //Inserts value to specific table and field
                s.executeUpdate("INSERT INTO " + Table + "(" + Utils.arrayToList(Columns) + ") VALUES(" + Utils.arrayToList(Values) + ")");
        }

        public static void INSERT(Statement s, String Table, String[] Columns, float[] Values) throws SQLException { //Inserts value to specific table and field
                s.executeUpdate("INSERT INTO " + Table + "(" + Utils.arrayToList(Columns) + ") VALUES(" + Utils.arrayToList(Values) + ")");
        }

        public static void INSERT(Statement s, String Table, String[] Columns, double[] Values) throws SQLException { //Inserts value to specific table and field
                s.executeUpdate("INSERT INTO " + Table + "(" + Utils.arrayToList(Columns) + ") VALUES(" + Utils.arrayToList(Values) + ")");
        }

        public static void INSERT(Statement s, String Table, String[] Columns, boolean[] Values) throws SQLException { //Inserts value to specific table and field
                s.executeUpdate("INSERT INTO " + Table + "(" + Utils.arrayToList(Columns) + ") VALUES(" + Utils.arrayToList(Values) + ")");
        }

        public static void INSERT(Statement s, String Table, String[] Columns, ArrayList<String> Values) throws SQLException { //Inserts value to specific table and field
                s.executeUpdate("INSERT INTO " + Table + "(" + Utils.arrayToList(Columns) + ") VALUES(" + Utils.stringToList(Values) + ")");
        }

        /*===========================================================*/
        
        public static void UPDATE(Statement s, String Table, String Column, String Value, String Id, String IdValue) throws SQLException{
                s.executeUpdate("UPDATE " + Table + " SET "+ Column +" = \'"+ Value + "\' WHERE " + Id + " = \'" + IdValue + "\'");
        }
        
        public static void UPDATE(Statement s, String Table, String Column, String Value, String Id, int IdValue) throws SQLException{
                s.executeUpdate("UPDATE " + Table + " SET "+ Column +" = \'"+ Value + "\' WHERE " + Id + " = " + IdValue);
        }
        
        public static void UPDATE(Statement s, String Table, String Column, int Value, String Id, String IdValue) throws SQLException{
                s.executeUpdate("UPDATE " + Table + " SET "+ Column +" = \'"+ Value + "\' WHERE " + Id + " = \'" + IdValue + "\'");
        }
        
        public static void UPDATE(Statement s, String Table, String Column, int Value, String Id, int IdValue) throws SQLException{
                s.executeUpdate("UPDATE " + Table + " SET "+ Column +" = \'"+ Value + "\' WHERE " + Id + " = " + IdValue);
        }
        
        public static void UPDATE(Statement s, String Table, String Column, double Value, String Id, String IdValue) throws SQLException{
                s.executeUpdate("UPDATE " + Table + " SET "+ Column +" = \'"+ Value + "\' WHERE " + Id + " = \'" + IdValue + "\'");
        }
        
        public static void UPDATE(Statement s, String Table, String Column, double Value, String Id, int IdValue) throws SQLException{
                s.executeUpdate("UPDATE " + Table + " SET "+ Column +" = \'"+ Value + "\' WHERE " + Id + " = " + IdValue);
        }
        
        public static void UPDATE(Statement s, String Table, String Column, boolean Value, String Id, String IdValue) throws SQLException{
                s.executeUpdate("UPDATE " + Table + " SET "+ Column +" = \'"+ Value + "\' WHERE " + Id + " = \'" + IdValue + "\'");
        }
        
        public static void UPDATE(Statement s, String Table, String Column, boolean Value, String Id, int IdValue) throws SQLException{
                s.executeUpdate("UPDATE " + Table + " SET "+ Column +" = \'"+ Value + "\' WHERE " + Id + " = " + IdValue);
        }
        
        public static void UPDATE(Statement s, String Table, String Column, String Value) throws SQLException{
                s.executeUpdate("UPDATE " + Table + " SET " + Column + " = \'" + Value + "\'");
        }
        
        public static void UPDATE(Statement s, String Table, String Column, int Value) throws SQLException{
                s.executeUpdate("UPDATE " + Table + " SET " + Column + " = " + Value);
        }
        
        public static void UPDATE(Statement s, String Table, String Column, double Value) throws SQLException{
                s.executeUpdate("UPDATE " + Table + " SET " + Column + " = " + Value);
        }
        
        public static void UPDATE(Statement s, String Table, String Column, boolean Value) throws SQLException{
                s.executeUpdate("UPDATE " + Table + " SET " + Column + " = " + Value);
        }
        
        
        /*===========================================================*/
        
        public static int DELETE(Statement s, String Table, String Column, String Value) throws SQLException { //Deletes record
                if (!valueExists(s, Table, Column, Value)) {
                        System.out.println("Record doesn't exist");
                        return -1;
                }

                String query = "DELETE FROM " + Table + " WHERE " + Column + " = \'" + Value + "\'";

                s.executeUpdate(query);
                System.out.println("Record deleted");
                return 0;
        }

        public static int DELETE(Statement s, String Table) throws SQLException { //Clears Table
                String query = "DELETE FROM " + Table;
                s.executeUpdate(query);

                System.out.println("Table Cleared");
                return 0;
        }

        /*===========================================================*/
        
        public static ArrayList<String> SELECT(Statement s, String Table, String Column, String Value) throws SQLException {
                ArrayList<String> list = new ArrayList<>();

                String query = "SELECT " + Value + " FROM " + Table + " WHERE " + Column + "= \'" + Value + "\'";
                ResultSet rs = s.executeQuery(query);

                while (rs.next()) {

                        list.add(rs.getString(1));

                }

                return list;
        }
        
        public static ArrayList<Integer> SELECT(Statement s, String Table, String Column, int Value) throws SQLException {
                ArrayList<Integer> list = new ArrayList<>();

                String query = "SELECT " + Value + " FROM " + Table + " WHERE " + Column + "= '" + Value;
                ResultSet rs = s.executeQuery(query);

                while (rs.next()) {

                        list.add(rs.getInt(1));

                }

                return list;
        }
        
        public static ArrayList<Double> SELECT(Statement s, String Table, String Column, double Value) throws SQLException {
                ArrayList<Double> list = new ArrayList<>();

                String query = "SELECT " + Value + " FROM " + Table + " WHERE " + Column + "= '" + Value;
                ResultSet rs = s.executeQuery(query);

                while (rs.next()) {

                        list.add(rs.getDouble(1));

                }

                return list;
        }
        
        public static ArrayList<Boolean> SELECT(Statement s, String Table, String Column, boolean Value) throws SQLException {
                ArrayList<Boolean> list = new ArrayList<>();

                String query = "SELECT " + Value + " FROM " + Table + " WHERE " + Column + "= '" + Value;
                ResultSet rs = s.executeQuery(query);

                while (rs.next()) {

                        list.add(rs.getBoolean(1));

                }

                return list;
        }
        
        public static ArrayList<Float> SELECT(Statement s, String Table, String Column, float Value) throws SQLException {
                ArrayList<Float> list = new ArrayList<>();

                String query = "SELECT " + Value + " FROM " + Table + " WHERE " + Column + "= '" + Value;
                ResultSet rs = s.executeQuery(query);

                while (rs.next()) {

                        list.add(rs.getFloat(1));

                }

                return list;
        }

        public static ArrayList<String> SELECT(Statement s, String Table, String Column) throws SQLException {
                ArrayList<String> list = new ArrayList<>();

                String query = "SELECT " + Column + " FROM " + Table;
                ResultSet rs = s.executeQuery(query);

                while (rs.next()) {

                        list.add(rs.getString(1));

                }

                return list;
        }
        
        public static ArrayList<String[]> SELECT(Statement s, String Table, String[] Columns) throws SQLException {
                ArrayList<String[]> list = new ArrayList<>();

                String query = "SELECT " + Utils.arrayToList(Columns) + " FROM " + Table;
                ResultSet rs = s.executeQuery(query);

                while (rs.next()) {
                        String[] str = new String[Columns.length];
                        
                        for (int i = 0; i < Columns.length; i++) {
                                str[i] = rs.getString(i+1);
                        }
                        
                        list.add(str);
                }

                return list;
        }
        
        public static ArrayList<String[]> SELECT(Statement s, String Table, String[] Columns, String Column, String Value) throws SQLException {
                ArrayList<String[]> list = new ArrayList<>();

                String query = "SELECT " + Utils.arrayToList(Columns) + " FROM " + Table+ " WHERE " + Column + " = \'" +Value + "\'";
                ResultSet rs = s.executeQuery(query);

                while (rs.next()) {
                        String[] str = new String[Columns.length];
                        
                        for (int i = 0; i < Columns.length; i++) {
                                str[i] = rs.getString(i+1);
                        }
                        
                        list.add(str);
                }

                return list;
        }

        /*===========================================================*/
        
        public static boolean valueExists(Statement s, String table, String column, String value) throws SQLException {
                String query = "SELECT " + column + " FROM " + table + " WHERE " + column + "=\'" + value + "\'";

                ResultSet rs = s.executeQuery(query);
                if (rs.next()) {
                        return true;
                }

                return (false);
        }

        public static boolean valueExists(Statement s, String table, String column, int value) throws SQLException {
                String query = "SELECT " + column + " FROM " + table + " WHERE " + column + "=" + value;

                ResultSet rs = s.executeQuery(query);
                if (rs.next()) {
                        return true;
                }

                return (false);
        }

        public static boolean valueExists(Statement s, String table, String column, float value) throws SQLException {
                String query = "SELECT " + column + " FROM " + table + " WHERE " + column + "=" + value;

                ResultSet rs = s.executeQuery(query);
                if (rs.next()) {
                        return true;
                }

                return (false);
        }

        public static boolean valueExists(Statement s, String table, String column, boolean value) throws SQLException {
                String query = "SELECT " + column + " FROM " + table + " WHERE " + column + "=" + value;

                ResultSet rs = s.executeQuery(query);
                if (rs.next()) {
                        return true;
                }

                return (false);
        }

        /*===========================================================*/
        
        public static int numOfRecords(Statement s, String Table) throws SQLException {
                ResultSet rs = s.executeQuery("SELECT COUNT(*)  FROM " + Table);

                rs.next();
                return rs.getInt(1);
        }

        public static int numOfRecords(Statement s, String Table, String Column) throws SQLException {
                ResultSet rs = s.executeQuery("SELECT COUNT( " + Column + ")  FROM " + Table);

                rs.next();
                return rs.getInt(1);
        }

        /*===========================================================*/
        //Need of terminal
        public static void CREATE(Statement s, String name) throws SQLException { //Create table
                ArrayList<String> colNames = new ArrayList<>();
                ArrayList<String> dataTypes = new ArrayList<>();

                String again;
                int i = 1;
                do {
                        System.out.print("Column" + i + " name: ");
                        colNames.add(UserInput.getString());
                        String type;
                        do {
                                System.out.print("DataType" + i + ": ");
                                type = UserInput.getString();
                        } while (!Utils.correctType(type));
                        dataTypes.add(type);
                        i++;

                        System.out.print("Add another (y/n): ");
                        again = UserInput.getString();
                } while (again.equals("y"));

                String query = "CREATE TABLE " + name + " (" + Utils.tableColsFormat(colNames, dataTypes) + ")";
                s.executeUpdate(query);
                System.out.println("Table " + name + " created");
        }
        
        public static void CREATE(Statement s, String Table, String Column, String type) throws SQLException { //Create column in table
                if(!Utils.correctType(type)){
                        System.out.println("Incorrect data type");
                        return;
                }

                String query = "ALTER TABLE " + Table + " ADD " + Column + " " + type;
                System.out.println(query);
                s.executeUpdate(query);
                System.out.println("Column Added");
        }

}
