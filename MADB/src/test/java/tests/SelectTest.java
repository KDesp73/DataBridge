package tests;


import kdesp73.madb.Condition;
import kdesp73.madb.MADB;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Konstantinos
 */
public class SelectTest {
        
        @Test
        void test1() throws SQLException{
                MADB db = new MADB("C:\\Users\\Konstantinos\\Github-repos\\Java_Database_Methods\\MADB\\src\\main\\java\\accessDB\\test.accdb");
                
                assertEquals("Kostas", db.SELECT("Table1", "TextF", new Condition("IntF", 5)));
        }
}
