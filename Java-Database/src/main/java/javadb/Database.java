package javadb;

import java.sql.*;
import java.util.regex.Pattern;

public class Database {
        private static final String PACKAGE = "[Your Package]"; //!!!!!!
        private static final String DATABASE_NAME = "[Your database name]"; //!!!!!!
        
        
        private static final String FILEPATH = System.getProperty("user.dir").replaceAll(Pattern.quote("\\"), "/") + "/src/main/java/" + PACKAGE.replaceAll(Pattern.quote("."), "/") + "/"+ DATABASE_NAME+".accdb";
        private static String url = "jdbc:ucanaccess://" + FILEPATH;
        private java.sql.Connection conn;
        private Statement statement;

        public Database() throws SQLException {
                conn = DriverManager.getConnection(url);
                statement = conn.createStatement();
        }

        public String getPath() {
                return FILEPATH;
        }

        public Statement getStatement() {
                return statement;
        }
        
        public String getName() {
                return DATABASE_NAME;
        }
}
