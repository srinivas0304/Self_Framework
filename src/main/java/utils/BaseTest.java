package utils;

import org.testng.annotations.BeforeMethod;
import actitimePages.SigninPage;

public class BaseTest extends SeleniumBaseActionDriver {
	
	@BeforeMethod(alwaysRun = true)
	public void login() 
	{
		getBaseActionDriver().get(CommonHelper.getUrl());
		getBaseActionDriver().manage().window().maximize();
		/*
		 * SigninPage signInPage = new SigninPage();
		 * signInPage.loginIntoApplication(username, password);
		 */
	}
	protected void clearCookies() {
		
			getBaseActionDriver().deletecookies();
			commonUtil.waitInSeconds(1);
	
	}

	@Override
	protected void cleanPageObjects() {
		// TODO Auto-generated method stub

	}

}
