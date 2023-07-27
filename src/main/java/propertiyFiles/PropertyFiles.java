package propertiyFiles;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openqa.selenium.json.JsonException;
import org.testng.Assert;

public class PropertyFiles 
{
	private static Properties prop = new Properties();
	  
	  public static String currentURL;
	  
	  static String path = System.getProperty("user.dir") + "/src/test/resources/propertyFiles/propertycontent.properties";
	  
	  static String jsonPath = System.getProperty("user.dir") + "/src/test/resources/jsonReader/testdata.json";
	  
	  public static synchronized void initialize(String path) {
	    try {
	      FileReader fr = new FileReader(path);
	      prop.load(fr);
	    } catch (IOException e) {
	      e.printStackTrace();
	      Assert.assertTrue(true, "File not intialized ");
	    } catch (NullPointerException e1) {
	      e1.printStackTrace();
	    } 
	  }
	  
	  public static synchronized String getProperty(String value) {
	    return prop.getProperty(value);
	  }
	  
	  public static String getConfigProperty(String value) {
	    initialize(path);
	    String propvalue = getProperty(value);
	    return propvalue;
	  }
	  
	  public static String getConfigProperty(String path, String value) {
	    initialize(path);
	    String propvalue = getProperty(value);
	    return propvalue;
	  }
	  
	  public static void SetConfigProperty(String path, String key, String value) {
	    initialize(path);
	    prop.setProperty(key, value);
	  }
	  
	  public static String readJson(String path, String key) throws FileNotFoundException {
	    FileReader fr = new FileReader(path);
	    String keyvalue = null;
	    try {
	      JSONParser jsonparser = new JSONParser();
	      Object obj = jsonparser.parse(fr);
	      JSONObject usersobj = (JSONObject)obj;
	      JSONArray usersList = (JSONArray)usersobj.get("users");
	      JSONObject user = (JSONObject)usersList.get(0);
	      keyvalue = (String)user.get(key);
	    } catch (JsonException|IOException|org.json.simple.parser.ParseException e) {
	      e.printStackTrace();
	    } 
	    return keyvalue;
	  }
	  
	  public static String getJsonData(String key) throws FileNotFoundException {
	    String loginData = readJson(jsonPath, key);
	    return loginData;
	  }
}
