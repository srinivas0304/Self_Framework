package utils;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

public class TestListeners implements ITestListener {
	  private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
	  
	  private static ThreadLocal<CommonUtil> common = new ThreadLocal<>();
	  
	  private static ThreadLocal<BaseActionDriver> actionDriver = new ThreadLocal<>();
	  
	  public void onFinish(ITestContext arg0) {
	    if (CommonUtil.getExtentReports() != null)
	      CommonUtil.getExtentReports().flush(); 
	    test.remove();
	    common.remove();
	    actionDriver.remove();
	  }
	  
	  public void onStart(ITestContext arg0) {}
	  
	  public void onTestFailedButWithinSuccessPercentage(ITestResult arg0) {}
	  
	  public void onTestFailure(ITestResult arg0) {
	    actionDriver.remove();
	    common.remove();
	    test.remove();
	    SeleniumBaseActionDriver currentClass = (SeleniumBaseActionDriver)arg0.getInstance();
	    actionDriver.set(SeleniumBaseActionDriver.getBaseActionDriver());
	    common.set(SeleniumBaseActionDriver.getCommonUtil());
	    test.set(SeleniumBaseActionDriver.getTest());
	    ((CommonUtil)common.get()).log(String.valueOf(arg0.getMethod().getMethodName()) + " Failed!");
	    ((CommonUtil)common.get()).failSeleniumTest(arg0.getThrowable(), actionDriver.get());
	    currentClass.assignTag();
	    currentClass.assignAuthor();
	  }
	  
	  public void onTestSkipped(ITestResult arg0) {
	    CommonUtil commonUtil = SeleniumBaseActionDriver.getCommonUtil();
	    commonUtil.log(String.valueOf(arg0.getMethod().getMethodName()) + " skipped!");
	    if (commonUtil.isDriverInitialized()) {
	      commonUtil.log("Removing skipped test from Report....");
	      CommonUtil.getExtentReports().removeTest(SeleniumBaseActionDriver.getTest());
	    } else {
	      commonUtil.skipSeleniumTest(arg0.getThrowable(), SeleniumBaseActionDriver.getBaseActionDriver());
	    } 
	  }
	  
	  public void onTestStart(ITestResult arg0) {}
	  
	  public void onTestSuccess(ITestResult arg0) {
	    CommonUtil commonUtil = SeleniumBaseActionDriver.getCommonUtil();
	    commonUtil.log("onTestSuccess....");
	    SeleniumBaseActionDriver.getTest().log(Status.PASS, "Test passed");
	    SeleniumBaseActionDriver currentClass = (SeleniumBaseActionDriver)arg0.getInstance();
	    currentClass.assignTag();
	    currentClass.assignAuthor();
	  }
}