
package javadb;

import java.util.ArrayList;

public class Utils {
       /*===========DataBase Utils===========*/
        public static String arrayToList(int[] arr) {
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
        
        public static String arrayToList(float[] arr) {
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
        
        public static String arrayToList(double[] arr) {
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
        
        public static String arrayToList(boolean[] arr) {
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

        public static String arrayToList(String[] arr) {
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
        
        public static String stringToList(String[] arr) {
                String s = "";

                for (int i = 0; i < arr.length; i++) {
                        if (i == arr.length - 1) {
                                s = s.concat("\""+arr[i] + "\"");
                        } else {
                                s = s.concat("\""+arr[i] + "\", ");
                        }
                }

                return s;
        }


        public static String stringToList(ArrayList<String> arr) {
                String s = "";

                for (int i = 0; i < arr.size(); i++) {
                        if (i == arr.size() - 1) {
                                s = s.concat("\""+arr.get(i) + "\"");
                        } else {
                                s = s.concat("\""+arr.get(i) + "\", ");
                        }
                }

                return s;
        }
        
        public static boolean correctType(String type){
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
                
                for (int i = 0; i < types.length; i++) {
                        if (type.equals(types[i])) {
                                return true;
                        }
                }
                return false;
        }
        
        public static String tableColsFormat(ArrayList<String> cols, ArrayList<String> types){
                String s="";
                for (int i = 0; i < cols.size(); i++) {
                        if(i == cols.size()-1) s = s.concat(cols.get(i)+ " " +types.get(i));
                        else s = s.concat(cols.get(i)+ " " +types.get(i) + ", ");
                        
                }
                return s;
        }
}
