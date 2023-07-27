package actitimePages;

import org.openqa.selenium.By;
import utils.BaseActionDriver;
import utils.CommonUtil;
import utils.SeleniumBaseActionDriver;

public class SigninPage 
{
	BaseActionDriver actionDriver;
	CommonUtil commonUtil;

	public SigninPage() 
	{
		actionDriver = SeleniumBaseActionDriver.getBaseActionDriver();
		commonUtil = SeleniumBaseActionDriver.getCommonUtil();
	}
	
	private By usernameTextbox = By.xpath("//input[@id='username']");
	private By passwordTextbox = By.xpath("//input[@name='pwd']");
	private By signInButton = By.xpath("//div[text()='Login ']");
	private By verifyLogo = By.xpath("//div[@id='logo_aT']");
	
	public static final String PAGE_TITLE = "actiTIME - Login";
	
	public void loginIntoApplication(String username, String password)
	{
		actionDriver.click(usernameTextbox);
		commonUtil.waitInSeconds(2);
		actionDriver.enterTestData(usernameTextbox, username);
		actionDriver.type(passwordTextbox, password);
		actionDriver.click(signInButton);
		commonUtil.waitInSeconds(8);
		if(actionDriver.isElementPresent(verifyLogo, 15))
		{
			System.out.println("the element is present: "+verifyLogo);
			commonUtil.waitInSeconds(6);
		}
	}
}
