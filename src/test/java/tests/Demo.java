package tests;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import testrail.TestRaild;
import utils.BaseTest;
import utils.TestInfo;

public class Demo extends BaseTest
{
	@BeforeClass(alwaysRun = true)
	public void initialSetup()
	{
		initializeWebTest("Title verification");
	}
	@TestInfo(testRailId = TestRaild.C1)
	@Test(priority = 1)
	public void test()
	{
		System.out.println("Update in test rail.....");
	}
	
	@TestInfo(testRailId = TestRaild.C2)
	@Test(priority = 2)
	public void test1()
	{
		System.out.println("Update in test rail.....");
	}
}
