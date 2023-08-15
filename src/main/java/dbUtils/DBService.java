package dbUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import com.mysql.cj.jdbc.result.ResultSetMetaData;

public class DBService 
{
	       // Connection object
	       static Connection con = null;
	       // Statement object
	       private static Statement stmt;
	       // Constant for Database URL
	     
	       public static String DB_URL = "jdbc:mysql://localhost:3306/world";   
	       // Constant for Database Username
	       public static String DB_USER = "root";
	       // Constant for Database Password
	       public static String DB_PASSWORD = "Password@7725";
	 
	       @BeforeTest
	       public void setUp() throws Exception {
	              try{
	                     // Make the database connection
	            	  	 Class.forName("com.mysql.cj.jdbc.Driver");
	                     // Get connection to DB
	                     Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
	                     // Statement object to send the SQL statement to the Database
	                     stmt = con.createStatement();
	                     }
	                     catch (Exception e)
	                     {
	                           e.printStackTrace();
	                     }
	       }
	 
	       @Test
	       public void test() {
	              try{
	              String query = "select * from city";
	              // Get the contents of userinfo table from DB
	              ResultSet rs = stmt.executeQuery(query);
	              // Print the result untill all the records are printed
	              // res.next() returns true if there is any next record else returns false
	  			  ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData();
	              int columnsNumber = rsmd.getColumnCount();
	              while (rs.next()) 
	  			{
	  			//Print one row          
	  			for(int i = 1 ; i <= columnsNumber; i++)
	  			{
	  			      System.out.print(rs.getString(i) + " "); //Print one element of a row
	  			}

	  			  System.out.println();//Move to the next line to print the next row. 
	  			}
	              }
	              catch(Exception e)
	              {
	                     e.printStackTrace();
	              }     
	       }
	 
	       @AfterTest
	       public void tearDown() throws Exception {
	              // Close DB connection
	              if (con != null) {
	              con.close();
	              }
	       }
	
}
