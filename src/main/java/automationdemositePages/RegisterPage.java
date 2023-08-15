package automationdemositePages;

import java.io.FileReader;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openqa.selenium.By;
import utils.BaseActionDriver;
import utils.CommonUtil;
import utils.SeleniumBaseActionDriver;

public class RegisterPage 
{
	BaseActionDriver actionDriver;
	CommonUtil commonUtil;

	public RegisterPage() 
	{
		actionDriver = SeleniumBaseActionDriver.getBaseActionDriver();
		commonUtil = SeleniumBaseActionDriver.getCommonUtil();
	}
	
	private By firstName = By.xpath("//input[@placeholder='First Name']");
	private By lastName = By.xpath("//input[@placeholder='Last Name']");
	private By address = By.xpath("//textarea[@ng-model='Adress']");
	private By emailAddress = By.xpath("//input[@type='email']");
	private By phone = By.xpath("//input[@type='tel']");
	private By languages = By.xpath("//div[@id='msdd']");
	private By password = By.xpath("//input[@id='firstpassword']");
	private By confirmPassword = By.xpath("//input[@id='secondpassword']");
	
	public static final String PAGE_TITLE = "Register";
	
	public void loginIntoApplication(String firstname, String lastname, String commAddress, String email, String mobile, String languagesList, String firstPassword, String secondPassword)
	{
		actionDriver.click(firstName);
		commonUtil.waitInSeconds(2);
		actionDriver.enterTestData(firstName, firstname);
		actionDriver.type(lastName, lastname);
		actionDriver.type(address, commAddress);
		actionDriver.type(emailAddress, email);
		actionDriver.type(phone, mobile);
		actionDriver.type(languages, languagesList);
		actionDriver.type(password, firstPassword);
		actionDriver.type(confirmPassword, secondPassword);
		commonUtil.waitInSeconds(8);
		
		/*
		 * if(actionDriver.isElementPresent(verifyLogo, 15)) {
		 * System.out.println("the element is present: "+verifyLogo);
		 * commonUtil.waitInSeconds(6); }
		 */
	}
	
	public void loginfromJson() throws Exception
	{
		Thread.sleep(2000);
		
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader("C://Users//SrinivasAddla//Downloads//FrameworkArchi//src//test//resources//testdata//logindata.json"));
		String usrname= (String)jsonObject.get("username");
		String passwrd= (String)jsonObject.get("password");
//		actionDriver.getElement(usernameTextbox).sendKeys(usrname);
//		actionDriver.getElement(passwordTextbox).sendKeys(passwrd);
//		actionDriver.click(signInButton);
		Thread.sleep(2000);
	}
}
