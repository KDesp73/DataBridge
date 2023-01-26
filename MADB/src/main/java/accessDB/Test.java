package accessDB;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Test {

        public static void main(String[] args) throws SQLException {


                MADB db = new MADB("C:\\Users\\Konstantinos\\Github-repos\\Java_Database_Methods\\MADB\\src\\main\\java\\accessDB\\test.accdb");

                db.UPDATE("Table", "Text_Column", "Mpamphs", new Condition("Number_Column", 20));
        }
}
