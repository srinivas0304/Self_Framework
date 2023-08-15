package utils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import com.aventstack.extentreports.ExtentTest;

import testrail.TestRailUtil;

	@Listeners({TestListeners.class})
	public abstract class SeleniumBaseActionDriver 
	{
	  protected BaseActionDriver baseActionDriver;
	  
	  protected WebBaseActionDriver webActionDriver;
	  
	  protected CommonUtil commonUtil;
	  
	  protected String browserName;
	  
	  protected String testCaseName;
	  
	  public static int WAIT_TIMEOUT;
	  
	  public static Duration WAIT_TIMEOUT_DURATION;
	  
	  private static ThreadLocal<BaseActionDriver> baseActionDriverThread = new ThreadLocal<>();
	  
	  private static ThreadLocal<WebBaseActionDriver> webActionDriverThread = new ThreadLocal<>();
	  
	  private static ThreadLocal<CommonUtil> commonThread = new ThreadLocal<>();
	  
	  private static ThreadLocal<ExtentTest> testThread = new ThreadLocal<>();
	  
	  private static ThreadLocal<ExtentReportLogger> extentReportLoggerThread = new ThreadLocal<>();
	  
	  private static ThreadLocal<Method> method = new ThreadLocal<>();
	  
	  private static ThreadLocal<String> testRailCaseId = new ThreadLocal<>();
	  
	  private String testFeatureName;
	  
	  private CommonUtil.Platform platform;
	  
	  private static Map<String, String> testParams = new HashMap<>();
	  
	  protected String EXCEL_SHEET_PATH;
	  
	  protected abstract void cleanPageObjects();
	  
	  public static Map<String, String> getTestParamsMap() {
	    return testParams;
	  }
	  
	  public String getTestCaseName() {
	    return this.testCaseName;
	  }
	  
	  public static ExtentTest getTest() {
	    return testThread.get();
	  }
	  
	  public static CommonUtil getCommonUtil() {
	    return commonThread.get();
	  }
	  
	  public static WebBaseActionDriver getWebActionDriver() {
	    return webActionDriverThread.get();
	  }
	  
	  public static BaseActionDriver getBaseActionDriver() {
	    return baseActionDriverThread.get();
	  }
	  
	  public static ExtentReportLogger getExtentReportLogger() {
	    return extentReportLoggerThread.get();
	  }
	  
	  @Parameters({"sheet_name", "site"})
	  @BeforeTest
	  public void beforeTestMethod(@Optional String sheetName, @Optional String site) {
	    if (sheetName != null)
	      getTestParamsMap().put("sheet_name", sheetName); 
	    if (site != null)
	      getTestParamsMap().put("site", site); 
	  }
	  
	  public void initializeTestParameters(String browser, String site, String closeBrowserAfterTest, String runOnGrid, String retryAttempt, String sheetName, String testRailRunId, String gridUrl, String cloudAndroidDeviceName, String cloudIOSDeviceName, String db_conn, String db_url, String db_user,String db_pass) {
	    if (sheetName != null)
	      getTestParamsMap().put("sheet_name", sheetName); 
	    if (browser != null)
	      getTestParamsMap().put("browser_name", browser); 
	    if (site != null)
	      getTestParamsMap().put("site", site); 
	    if (closeBrowserAfterTest != null)
	      getTestParamsMap().put("closebrowser_after_test", closeBrowserAfterTest); 
	    if (runOnGrid != null)
	      getTestParamsMap().put("run_on_grid", runOnGrid); 
	    if (retryAttempt != null)
	      getTestParamsMap().put("retry_attempt", retryAttempt); 
	    if (testRailRunId != null)
	      getTestParamsMap().put("testrail_run_id", testRailRunId); 
	    if (gridUrl != null)
	      getTestParamsMap().put("grid_hub_url", gridUrl); 
	    if (cloudAndroidDeviceName != null)
	      getTestParamsMap().put("CLOUD_ANDROID_MOBILE_DEVICES", cloudAndroidDeviceName); 
	    if (cloudIOSDeviceName != null)
	      getTestParamsMap().put("CLOUD_IOS_MOBILE_DEVICES", cloudIOSDeviceName);
	    if(db_conn != null)
	    	getTestParamsMap().put("DB_CONNECTION", db_conn);
	    if(db_url != null)
	    	getTestParamsMap().put("DB_URL", db_url);
	    if(db_user != null)
	    	getTestParamsMap().put("DB_USERNAME", db_user);
	    if(db_pass != null)
	    	getTestParamsMap().put("DB_PASSWORD", db_pass);	
	  }
	  
	  @Parameters({"browser_name", "site", "closebrowser_after_test", "run_on_grid", "retry_attempt", "sheet_name", "testrail_run_id", "grid_hub_url", "CLOUD_ANDROID_MOBILE_DEVICES", "CLOUD_IOS_MOBILE_DEVICES","DB_CONNECTION","DB_URL","DB_USERNAME","DB_PASSWORD"})
	  @BeforeMethod(alwaysRun = true)
	  public synchronized void beforeMethod(@Optional String browser, @Optional String site, @Optional String closeBrowserAfterTest, @Optional String runOnGrid, @Optional String retryAttempt, @Optional String sheetName, @Optional String testRailRunId, @Optional String gridUrl, @Optional String cloudAndroidDeviceName, @Optional String cloudIOSDeviceName, @Optional String db_conn, @Optional String db_url, @Optional String db_user, @Optional String db_pass, Method method, ITestContext ctx) throws Exception {
	    System.out.println("In Framework Before Method...");
	    if (getBaseActionDriver() == null) {
	      if (this.platform == CommonUtil.Platform.DESKTOP) {
	        CommonUtil.setInitialConfigurations(CommonUtil.Platform.DESKTOP);
	        baseActionDriverThread.set(new WebBaseActionDriver());
	      } 
	      else if(this.platform == CommonUtil.Platform.DB)
	      {
	    	  CommonUtil.setInitialConfigurations(CommonUtil.Platform.DB);
	          baseActionDriverThread.set(new WebBaseActionDriver());
	      }
	      else {
	        Assert.assertTrue(false, "Platform not set. Please set to dekstop or mobile..");
	      } 
	      commonThread.set(new CommonUtil(getBaseActionDriver()));
	      getBaseActionDriver().initializeLogging();
	    } 
	    this.baseActionDriver = getBaseActionDriver();
	    this.commonUtil = getCommonUtil();
	    getCommonUtil().setTestCaseId(this.testFeatureName);
	    SeleniumBaseActionDriver.method.set(method);
	    initializeTestParameters(browser, site, closeBrowserAfterTest, runOnGrid, retryAttempt, sheetName, testRailRunId, gridUrl, cloudAndroidDeviceName, cloudIOSDeviceName,db_conn,db_url,db_user,db_pass);
	    String browserName = CommonUtil.getConfigProperty("browser_name");
	    getBaseActionDriver().setBrowserName(browserName);
	    getBaseActionDriver().setTestCaseName(method.getName());
	    testThread.set(CommonUtil.getExtentReports().createTest(String.valueOf(browserName) + " " + String.valueOf(browserName) + "-" + getCommonUtil().getTestCaseId(), ""));
	    getCommonUtil().initializeLogs(getTest());
	    getCommonUtil().log("Execute test case " + method.getName());
	    extentReportLoggerThread.set(new ExtentReportLogger(getTest()));
	    if (!getBaseActionDriver().isDriverSessionActive()) {
	      getCommonUtil().initializeDriver(browserName);
	      webActionDriverThread.set((WebBaseActionDriver)getBaseActionDriver());
	      this.webActionDriver = webActionDriverThread.get();
	      if (method.isAnnotationPresent((Class)TestInfo.class)) {
	        TestInfo testInfo = method.<TestInfo>getAnnotation(TestInfo.class);
	        ctx.setAttribute("caseId", testInfo.testRailId());
	        testRailCaseId.set(testInfo.testRailId());
	      } 
	    }
	    
	  }
	  
	  @AfterMethod(alwaysRun = true)
	  public synchronized void afterMethod(ITestResult result) throws Exception {
	    getCommonUtil().log("In After Method..");
	    if (result.getStatus() != 3 && result.getStatus() != 1 && result
	      .getStatus() != 2) {
	      getCommonUtil().log("Skipping and updating report as Test execution is crashed..");
	      getCommonUtil().skipSeleniumTest(null, getBaseActionDriver());
	    } 
	    CommonUtil.flushExtentReport();
	    (new TestRailUtil(getExtentReportLogger())).updateStatusOnTestRail(result, testRailCaseId.get());
	    if (Boolean.parseBoolean(CommonUtil.getConfigProperty("closebrowser_after_test"))) {
	      closeBrowser();
	      webActionDriverThread.remove();
	      commonThread.remove();
	      baseActionDriverThread.remove();
	    } 
	    extentReportLoggerThread.remove();
	    testThread.remove();
	    testRailCaseId.remove();
	    method.remove();
	  }
	  
	  @AfterSuite(alwaysRun = true)
	  public void postScriptsCleaning() throws IOException {
	    if (!Boolean.parseBoolean(CommonUtil.getConfigProperty("closebrowser_after_test")))
	      closeBrowser(); 
	    baseActionDriverThread.remove();
	    webActionDriverThread.remove();
	    commonThread.remove();
	  }
	  
	  public void initializeMobileTest(String testFeatureName) throws Exception {
	    this.testFeatureName = testFeatureName;
	    this.platform = CommonUtil.Platform.MOBILE;
	  }
	  
	  public void initializeWebTest(String testFeatureName) {
	    this.testFeatureName = testFeatureName;
	    this.platform = CommonUtil.Platform.DESKTOP;
	  }
	  
	  public void initializeDbTest(String testFeatureName)
	  {
		  this.testFeatureName = testFeatureName;
		    this.platform = CommonUtil.Platform.DB;
	  }
	  
	  public void closeBrowser() {
	    getBaseActionDriver().closeBrowser();
	    cleanPageObjects();
	  }
	  
	  @DataProvider
	  public Object[][] getData(Method method) {
	    return (new DataProviderUtil()).getData(this.EXCEL_SHEET_PATH, getCommonUtil().getTestCaseId(), method.getName());
	  }
	  
	  public void assignAuthor() {
	    if (((Method)method.get()).isAnnotationPresent((Class)TestInfo.class)) {
	      TestInfo testInfo = ((Method)method.get()).<TestInfo>getAnnotation(TestInfo.class);
	      if (!testInfo.author().equalsIgnoreCase("none"))
	        getTest().assignAuthor(new String[] { testInfo.author() }); 
	    } 
	  }
	  
	  public void assignTag() {
	    getTest().assignCategory(new String[] { getCommonUtil().getTestCaseId() });
	  }
	

}
