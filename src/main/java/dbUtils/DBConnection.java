package dbUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.mysql.cj.jdbc.result.ResultSetMetaData;
import utils.CommonUtil;
import utils.SeleniumBaseActionDriver;

public class DBConnection extends SeleniumBaseActionDriver
{
	 // Connection object
    static Connection con = null;
    // Statement object
    public static Statement stmt;
    // Constant for Database URL
    
    @BeforeClass(alwaysRun = true)
	public void initialSetup()
	{
    	initializeDbTest("Connect to DB...");
	}
    @Test
    public void connection() throws Exception {
    	try{
        	  
                  // Make the database connection
    		Class.forName(CommonUtil.getConfigProperty("db_connection"));
                  // Get connection to DB
                  Connection con = 
                		  DriverManager.getConnection(CommonUtil.getConfigProperty("db_url"),
                				  CommonUtil.getConfigProperty("db_username"),CommonUtil.getConfigProperty("db_password"));
                 
                  if(!con.isClosed())
                  {
                	  System.out.println("Successfully connected to the database");
                	  stmt = con.createStatement();
                	  ResultSet rs = stmt.executeQuery("select * from orderitems");
                	  ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData();
                	  int columnsNumber = rsmd.getColumnCount();
                	  while(rs.next())
                	  {
                		//Print one row          
          	  			for(int i = 1 ; i <= columnsNumber; i++)
          	  			{
          	  			      System.out.print(rs.getString(i) + " "); //Print one element of a row
          	  			}

          	  			  System.out.println();//Move to the next line to print the next row. 
          	  			}
                  }       
                  }
                  catch (Exception e)
                  {
                        e.printStackTrace();
                  }
//           			finally {
//           				try {
//           					con.close();
//           				}
//           				catch(SQLException e)
//           				{
//           					e.printStackTrace();
//           				}
//           				try {
//           					if(con.isClosed())
//           					{
//           						System.out.println("Connection to city database is closed");
//           					}
//           				}
//           					catch(SQLException e)
//           					{
//           						e.printStackTrace();
//           					}
//           				}
           			}

	@Override
	protected void cleanPageObjects() {
		// TODO Auto-generated method stub
		
	}
}
