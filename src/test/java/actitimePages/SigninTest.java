package actitimePages;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import testrail.TestRaild;
import utils.BaseTest;
import utils.CommonUtil;
import utils.TestInfo;

public class SigninTest extends BaseTest
{
	@BeforeClass(alwaysRun = true)
	public void initialSetup()
	{
		initializeWebTest("This is login feature...");
	}
	
	@TestInfo(testRailId = TestRaild.C3)
	@Test(enabled = false)
	public void loginTest()
	{
		SigninPage sign = new SigninPage();
		sign.loginIntoApplication(CommonUtil.getConfigProperty("user_name"),CommonUtil.getConfigProperty("pass_word"));
	}
	
	@Test
	public void loginTest2 () throws Exception
	{
		SigninPage sign = new SigninPage();
		sign.loginfromJson();
	}
	
}
