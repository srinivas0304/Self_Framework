package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.testng.Assert;
import org.testng.Reporter;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.observer.ExtentObserver;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import enums.Browser;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.bonigarcia.wdm.config.OperatingSystem;

public class CommonUtil {
  private ChromeOptions chromeOptions;
  
  private SafariOptions safariOptions;
  
  private EdgeOptions edgeOptions;
  
  private DesiredCapabilities capabilities;
  
  private BaseActionDriver actionDriver;
  
  private String sessionId;
  
  public static String MAIN_DIR;
  
  public static Properties TEST_ENV;
  
  public static Properties CONFIG;
  
  public static Properties MOBILE_CONFIG;
  
  public static boolean isInitialConfigurationDone = false;
  
  public static boolean isReportInitialized = false;
  
  public static String ENVIRONMENT_UNDER_TEST;
  
  private static ExtentReports extentReports;
  
  private static String OS = System.getProperty("os.name").toLowerCase();
  
  private String screenShotName = null;
  
  private String testCaseId = null;
  
  private ExtentTest test;
  
  private String browserName;
  
  private boolean driverInitialized = false;
  
  private Platform platform;
  
  public CommonUtil(BaseActionDriver actionDriver) {
    this.actionDriver = actionDriver;
  }
  
  public CommonUtil() {}
  
  public Platform getPlatform() {
    return this.platform;
  }
  
  public CommonUtil(String featureName, ExtentTest extentTest) {
    this.test = extentTest;
    this.testCaseId = featureName;
  }
  
  public static ExtentReports getExtentReports() {
    return extentReports;
  }
  
  public boolean isDriverInitialized() {
    return this.driverInitialized;
  }
  
  public String getTestCaseId() {
    return this.testCaseId;
  }
  
  public void setTestCaseId(String testCaseId) {
    this.testCaseId = testCaseId;
  }
  
  public static synchronized void flushExtentReport() {
    getExtentReports().flush();
  }
  
  public enum Platform {
    DESKTOP, MOBILE, API;
  }
  
  public static synchronized void loadConfigProp() throws IOException {
    String env = null;
    String baseFilePath = DIRECTORY.CONFIG_DIR;
    if (System.getProperty("test.env") == null) {
      System.out.println("Reading environment name from property file....");
      System.out.println("Testenv property file path: " + baseFilePath + "testEnv.properties");
      TEST_ENV = new Properties();
      FileInputStream fileInputStream = new FileInputStream(String.valueOf(baseFilePath) + "testEnv.properties");
      TEST_ENV.load(fileInputStream);
      env = TEST_ENV.getProperty("testEnv").toLowerCase();
    } else {
      System.out.println("Reading environment name from Command line....");
      env = System.getProperty("test.env");
      System.out.println("Environment selected: " + env);
    } 
    ENVIRONMENT_UNDER_TEST = env;
    CONFIG = new Properties();
    String filePath = String.valueOf(baseFilePath) + "config-" + ENVIRONMENT_UNDER_TEST + ".properties";
    FileInputStream fn = new FileInputStream(filePath);
    CONFIG.load(fn);
  }
  
  public static synchronized void setInitialConfigurations(Platform platform) {
    if (!isInitialConfigurationDone) {
      isInitialConfigurationDone = true;
      System.out.println("Initializing Properties files..");
      try {
        String baseFilePath = DIRECTORY.CONFIG_DIR;
        loadConfigProp();
        if (platform != Platform.DESKTOP && platform != Platform.API) {
          MOBILE_CONFIG = new Properties();
          MOBILE_CONFIG.load(new FileInputStream(String.valueOf(baseFilePath) + "mobile-config.properties"));
        } 
        SeleniumBaseActionDriver.WAIT_TIMEOUT = Integer.parseInt(getConfigProperty("selenium_timeout"));
      } catch (IOException e) {
        Assert.assertTrue(false, "Environment configuration is not set: " + e.getMessage());
      } 
    } 
    initializeExtentReport();
  }
  
  public static synchronized void initializeExtentReport() {
    if (!isReportInitialized) {
      isReportInitialized = true;
      cleanOldReports();
      System.out.println("Setting up extent Report object");
      ExtentSparkReporter htmlReporter = new ExtentSparkReporter(String.valueOf(DIRECTORY.HTML_REPORT_PATH) + "/index.html");
      extentReports = new ExtentReports();
      extentReports.attachReporter(new ExtentObserver[] { (ExtentObserver)htmlReporter });
    } 
  }
  
  public void initializeDriver(Browser browser) throws MalformedURLException {
    initializeDriver(browser.toString());
  }
  
  public void initializeDriver(String browser) throws MalformedURLException {
    log("Initializing driver !!");
    this.browserName = browser;
    this.actionDriver.setBrowserName(browser);
   if (browser.toLowerCase().contains(Browser.CHROME.toString().toLowerCase())) {
      log("Setting capabilities for DesktOP Chrome Browser....");
      this.chromeOptions = new ChromeOptions();
//      this.chromeOptions.setCapability("ensureCleanSession", true);
//      this.chromeOptions.setCapability("acceptSslCerts", true);
      this.chromeOptions.setCapability("acceptInsecureCerts", true);
      if (Boolean.parseBoolean(getConfigProperty("run_on_grid"))) {
        String hubUrl = getConfigProperty("grid_hub_url");
        log("Hub Url: " + hubUrl);
        String tunnel_id = getConfigProperty("tunnel_id");
        
        if (hubUrl != null && hubUrl.contains("saucelab") && tunnel_id != null && tunnel_id.length() > 3) {
          log("Using tunnel id: " + tunnel_id);
          
          String accessKey = getConfigProperty("accessKey");
          this.chromeOptions.setPlatformName(getConfigProperty("could_platform_name"));
          this.chromeOptions.setBrowserVersion("latest");
  		Map<String, Object> sauceOptions = new HashMap<>();
  		sauceOptions.put("username", tunnel_id);
  		sauceOptions.put("accessKey", accessKey);
  		sauceOptions.put("build", "selenium-build-22EFO");
  		String testname=this.actionDriver.getTestCaseName();
  		sauceOptions.put("name", testname);
  		this.chromeOptions.setCapability("sauce:options", sauceOptions);

  		//          this.chromeOptions.setCapability("name", this.actionDriver.getTestCaseName());
//          this.chromeOptions.setCapability("tunnelIdentifier", tunnel_id);
          
        } 
        ((WebBaseActionDriver)this.actionDriver).setWebDriver(new RemoteWebDriver(new URL(hubUrl), (Capabilities)this.chromeOptions));
        log("Chrome driver initialized on Hub !!");
      } else {
        initializeChromeDriverServer();
        this.chromeOptions.addArguments("--remote-allow-origins=*");
        ((WebBaseActionDriver)this.actionDriver).setWebDriver((RemoteWebDriver)new ChromeDriver(this.chromeOptions));
        log("Chrome driver initialized on local device !!");
      } 
    } 
   else if (browser.toLowerCase().contains(Browser.FIREFOX.toString().toLowerCase())) {
      FirefoxOptions firefoxOption = new FirefoxOptions();
      firefoxOption = new FirefoxOptions();
//      firefoxOption.setCapability("unexpectedAlertBehaviour", UnexpectedAlertBehaviour.ACCEPT);
//      firefoxOption.setCapability("acceptSslCerts", true);
      if (Boolean.parseBoolean(getConfigProperty("run_on_grid"))) {
        String hubUrl = getConfigProperty("grid_hub_url");
        log("Hub Url: " + hubUrl);
        String tunnel_id = getConfigProperty("tunnel_id");
        if (hubUrl != null && hubUrl.contains("saucelab") && tunnel_id != null && tunnel_id.length() > 3) {
          log("Using tunnel id: " + tunnel_id);

 //          firefoxOption.setCapability("tunnelIdentifier", tunnel_id);
//          firefoxOption.setCapability("name", this.actionDriver.getTestCaseName());
          
          String accessKey = getConfigProperty("accessKey");
          firefoxOption.setPlatformName(getConfigProperty("could_platform_name"));
          firefoxOption.setBrowserVersion("latest");
  		Map<String, Object> sauceOptions = new HashMap<>();
  		sauceOptions.put("username", tunnel_id);
  		sauceOptions.put("accessKey", accessKey);
  		sauceOptions.put("build", "selenium-build-22EFO");
  		String testname=this.actionDriver.getTestCaseName();
  		sauceOptions.put("name", testname);
  		firefoxOption.setCapability("sauce:options", sauceOptions);
  		
        } 
        ((WebBaseActionDriver)this.actionDriver).setWebDriver(new RemoteWebDriver(new URL(hubUrl), (Capabilities)firefoxOption));
        log("Firefox driver initialized on Hub !!");
      } else {
        initializeFirefoxDriverServer();
        ((WebBaseActionDriver)this.actionDriver).setWebDriver((RemoteWebDriver)new FirefoxDriver(firefoxOption));
        log("Firefox driver initialized on local device !!");
      } 
    } 
    else if (browser.toLowerCase().contains(Browser.MSEDGE.toString().toLowerCase())) {
    	this.edgeOptions = new EdgeOptions();
    	this.edgeOptions.setCapability("acceptInsecureCerts", true);
//    	this.edgeOptions.setCapability("unexpectedAlertBehaviour", UnexpectedAlertBehaviour.ACCEPT);
//       this.edgeOptions.setCapability("acceptSslCerts", true);
        if (Boolean.parseBoolean(getConfigProperty("run_on_grid"))) {
          String hubUrl = getConfigProperty("grid_hub_url");
          log("Hub Url: " + hubUrl);
          String tunnel_id = getConfigProperty("tunnel_id");
          if (hubUrl != null && hubUrl.contains("saucelab") && tunnel_id != null && tunnel_id.length() > 3) {
            log("Using tunnel id: " + tunnel_id);
           
//            this.edgeOptions.setCapability("tunnelIdentifier", tunnel_id);
//            this.edgeOptions.setCapability("name", this.actionDriver.getTestCaseName());
//            
            String accessKey = getConfigProperty("accessKey");
             this.edgeOptions.setPlatformName(getConfigProperty("could_platform_name"));
            this.edgeOptions.setBrowserVersion("latest");
  		Map<String, Object> sauceOptions = new HashMap<>();
  		sauceOptions.put("username", tunnel_id);
  		sauceOptions.put("accessKey", accessKey);
  		sauceOptions.put("build", "selenium-build-22EFO");
  		String testname=this.actionDriver.getTestCaseName();
  		sauceOptions.put("name", testname);
  		this.edgeOptions.setCapability("sauce:options", sauceOptions);
            
          } 
          ((WebBaseActionDriver)this.actionDriver).setWebDriver(new RemoteWebDriver(new URL(hubUrl), (Capabilities)edgeOptions));
          log("MS Edge driver initialized on Hub !!");
        } else {
        	initializeMsEdgeDriverServer();
        	((WebBaseActionDriver)this.actionDriver).setWebDriver((RemoteWebDriver)new EdgeDriver(this.edgeOptions));
          log("MS Edge driver initialized on local device !!");
        } 
      }
    else if (browser.toLowerCase().contains(Browser.SAFARI.toString().toLowerCase())) {
      log("Setting capabilities for Mac Safari Browser....");
      this.safariOptions = new SafariOptions();
      this.safariOptions.setCapability("browserVersion", "latest");
      if (Boolean.parseBoolean(getConfigProperty("run_on_grid"))) {
        String hubUrl = getConfigProperty("grid_hub_url");
        log("Hub Url: " + hubUrl);
        String tunnel_id = getConfigProperty("tunnel_id");
        if (hubUrl != null && hubUrl.contains("saucelab") && tunnel_id != null && tunnel_id.length() > 3) {
          log("Using tunnel id: " + tunnel_id);
          
//          this.safariOptions.setCapability("name", this.actionDriver.getTestCaseName());
//          MutableCapabilities sauceOptions = new MutableCapabilities();
//          sauceOptions.setCapability("tunnelIdentifier", tunnel_id);
//          this.safariOptions.setCapability("sauce:options", sauceOptions);
          
          String accessKey = getConfigProperty("accessKey");
          this.safariOptions.setPlatformName(getConfigProperty("could_platform_name"));
          this.safariOptions.setBrowserVersion("latest");
		Map<String, Object> sauceOptions = new HashMap<>();
		sauceOptions.put("username", tunnel_id);
		sauceOptions.put("accessKey", accessKey);
		sauceOptions.put("build", "selenium-build-22EFO");
		String testname=this.actionDriver.getTestCaseName();
		sauceOptions.put("name", testname);
		this.safariOptions.setCapability("sauce:options", sauceOptions);
		
        } 
        ((WebBaseActionDriver)this.actionDriver).setWebDriver(new RemoteWebDriver(new URL(hubUrl), (Capabilities)this.safariOptions));
        log("Safari driver initialized on Hub...");
        log("Desired Capabilities Used: " + this.safariOptions);
      } else {
        Assert.assertTrue(false, "Framework supports safari on saucelab only..");
      } 
    } else {
      log("Invalid browser. Check the config file");
      Assert.assertTrue(false, "Invalid browser. Check the config file");
    } 
    this.sessionId = this.actionDriver.getSessionId().toString();
    this.actionDriver.setImplicitWaitOnDriver(SeleniumBaseActionDriver.WAIT_TIMEOUT);
    this.driverInitialized = true;
  }
  
  private void initializeAndroidDriverOnSauceLab(String browserName) throws MalformedURLException {
    String deviceName = null;
    String appiumVersion = null;
    String hubUrl = getConfigProperty("grid_hub_url");
    if (hubUrl != null && hubUrl.length() < 3)
      hubUrl = CONFIG.getProperty("grid_hub_url"); 
   
    if (getMobileConfigProperty("CLOUD_APPIUM_VERSION") != null && 
      getMobileConfigProperty("CLOUD_APPIUM_VERSION").length() > 2) {
      appiumVersion = getMobileConfigProperty("CLOUD_APPIUM_VERSION");
      log("Setting Appium Version: " + appiumVersion);
    } 
    log("Device Selected: " + deviceName);
    String tunnel_id = getConfigProperty("tunnel_id");
    if (tunnel_id != null && tunnel_id.length() > 3) {
      log("Using tunnel id: " + tunnel_id);
      this.chromeOptions.setCapability("tunnelIdentifier", tunnel_id);
    } 
    if (browserName.toLowerCase().contains(Browser.ANDROID_CHROME.toString().toLowerCase())) {
      this.chromeOptions.setCapability("name", this.actionDriver.getTestCaseName());
      this.chromeOptions.setCapability("testobject_test_live_view_url", true);
      this.chromeOptions.setCapability("testobject_test_report_url", true);
      this.chromeOptions.setCapability("deviceName", deviceName);
      if (appiumVersion != null)
        this.chromeOptions.setCapability("appiumVersion", 
            getMobileConfigProperty("CLOUD_APPIUM_VERSION")); 
//      ((MobileActionDriver)this.actionDriver).setAndroidDriver(new AndroidDriver(new URL(hubUrl), (Capabilities)this.chromeOptions));
    } else {
      this.capabilities.setCapability("name", this.actionDriver.getTestCaseName());
      this.capabilities.setCapability("deviceName", deviceName);
      if (appiumVersion != null)
        this.capabilities.setCapability("version", 
            getMobileConfigProperty("CLOUD_APPIUM_VERSION")); 
      this.capabilities.setCapability("app", 
          getMobileConfigProperty("CLOUD_ANDROIDAPP_STORAGE"));
//      ((MobileActionDriver)this.actionDriver).setAndroidDriver(new AndroidDriver(new URL(hubUrl), (Capabilities)this.capabilities));
    } 
    log("Android driver initialized on sauce lab !!");
  }
  
  private void initializeIOSDriverOnSauceLab(MutableCapabilities capabilities) throws MalformedURLException {
    String deviceName, hubUrl = getConfigProperty("grid_hub_url");
    if (hubUrl != null && hubUrl.length() < 3)
      hubUrl = MOBILE_CONFIG.getProperty("grid_hub_url"); 
    capabilities.setCapability("browserVersion", "latest");
    capabilities.setCapability("name", this.actionDriver.getTestCaseName());
    capabilities.setCapability("testobject_test_live_view_url", true);
    capabilities.setCapability("testobject_test_report_url", true);
//    if (this.browserName.toLowerCase().contains(DeviceType.TABLET.toString())) {
//      deviceName = Device.getIpadNameOnCloud();
//    } else {
//      deviceName = Device.getIphoneNameOnCloud();
//    } 
//    log("Device Selected: " + deviceName);
//    capabilities.setCapability("deviceName", deviceName);
//    capabilities.setCapability("ensureCleanSession", true);
    String tunnel_id = getConfigProperty("tunnel_id");
    if (tunnel_id != null && tunnel_id.length() > 3) {
      capabilities.setCapability("tunnelIdentifier", tunnel_id);
      log("Using tunnel id: " + tunnel_id);
    } 
    if (getMobileConfigProperty("CLOUD_APPIUM_VERSION") != null && 
      getMobileConfigProperty("CLOUD_APPIUM_VERSION").length() > 2) {
      log("Setting Appium Version: " + 
          getMobileConfigProperty("CLOUD_APPIUM_VERSION"));
      capabilities.setCapability("appiumVersion", 
          getMobileConfigProperty("CLOUD_APPIUM_VERSION"));
    } 
//    ((MobileActionDriver)this.actionDriver).setIOSDriver(new IOSDriver(new URL(hubUrl), (Capabilities)capabilities));
    log("IOS driver initialized on sauce lab!!");
  }
  
  public static void cleanOldReports() {
    System.out.println("Cleaning older Html reports");
    File dir1 = new File(DIRECTORY.HTML_REPORT_PATH);
    boolean exists = dir1.exists();
    if (!exists) {
      System.out.println("the main directory you are searching does not exist : " + exists);
      dir1.mkdir();
    } else {
      System.out.println("the main directory you are searching does exist : " + exists);
    } 
    deleteDir(new File(DIRECTORY.HTML_REPORT_PATH));
  }
  
  public static boolean deleteDir(File dir) {
    if (dir.isDirectory()) {
      String[] children = dir.list();
      for (int i = 0; i < children.length; i++) {
        boolean success = (new File(dir, children[i])).delete();
        if (!success)
          return false; 
      } 
    } 
    return true;
  }
  
  public void failSeleniumTest(Throwable t, BaseActionDriver actionDriver) {
    log("Failing test..");
    String url = null;
    if (!this.browserName.toLowerCase().contains(Browser.ANDROID_NATIVE_APP.toString().toLowerCase()) || 
      this.browserName.toLowerCase().contains(Browser.IOS_NATIVE_APP.toString().toLowerCase()))
      try {
        url = actionDriver.getCurrentUrl();
      } catch (Throwable e) {
        log("Unable to fetch current url.");
      }  
    if (t == null) {
      this.test.log(Status.FAIL, "Script Execution crashed. Investigate error. Might be a Driver session issue.");
    } else {
      t.printStackTrace();
      this.test.log(Status.FAIL, t);
      if (url != null) {
        this.test.log(Status.INFO, "Fail: " + t.getMessage() + " | Url captured : " + url);
      } else {
        this.test.log(Status.INFO, "Fail: " + t.getMessage());
      } 
      addScreenShot(this.browserName);
    } 
  }
  
  public void skipSeleniumTest(Throwable throwable, BaseActionDriver actionDriver) {
    log("Skipping test..");
    String issue = null;
    if (throwable != null) {
      issue = throwable.getMessage();
      throwable.printStackTrace();
    } 
    String url = null;
    if (!this.browserName.toLowerCase().contains(Browser.ANDROID_NATIVE_APP.toString().toLowerCase()) || 
      this.browserName.toLowerCase().contains(Browser.IOS_NATIVE_APP.toString().toLowerCase()))
      try {
        url = actionDriver.getCurrentUrl();
      } catch (Throwable e) {
        log("Unable to fetch current url.");
      }  
    if (url != null) {
      this.test.log(Status.SKIP, "Skip: " + issue + " | Url captured : " + url);
    } else {
      this.test.log(Status.SKIP, "Skip: " + issue);
    } 
    addScreenShot(this.browserName);
  }
  
  public void addScreenShot(String browser) {
    log("Adding screenshot..");
    if (browser != null) {
      if (this.sessionId == null) {
        this.screenShotName = String.valueOf(this.testCaseId) + "_" + this.browserName + "_" + RandomUtil.getRandomNumber(12) + ".jpeg";
      } else {
        if (this.sessionId.contains(":"))
          this.sessionId = this.sessionId.split(":")[1].trim(); 
        this.screenShotName = String.valueOf(this.sessionId) + RandomUtil.getRandomNumber(18) + ".jpeg";
      } 
      String path = null;
      if (isUnix()) {
        path = "/HtmlReport/" + this.screenShotName;
      } else {
        path = String.valueOf(DIRECTORY.HTML_REPORT_PATH) + this.screenShotName;
      } 
      path = String.valueOf(DIRECTORY.HTML_REPORT_PATH) + this.screenShotName;
      log("Screenshot Path: " + path);
      this.actionDriver.takeScreenShot(path);
      try {
        this.test.log(Status.INFO, MediaEntityBuilder.createScreenCaptureFromPath(this.screenShotName).build());
      } catch (Throwable t) {
        log("Got error while attaching screenshot to extent report: " + path);
      } 
    } 
  }
  
  public void log(Object log) {
    Reporter.log(log.toString(), true);
    if (this.test != null)
      this.test.log(Status.INFO, log.toString()); 
  }
  
  public void initializeLogs(ExtentTest test) {
    this.test = test;
  }
  
  public enum PropertyFile {
    CONFIG, MOBILE_CONFIG;
  }
  
  public static synchronized String getConfigProperty(String key) {
    if (System.getProperty(key) != null) {
      System.out.println("Reading from commandline: " + key + ": " + System.getProperty(key));
      return System.getProperty(key);
    } 
    if (SeleniumBaseActionDriver.getTestParamsMap() != null && SeleniumBaseActionDriver.getTestParamsMap().get(key) != null)
      return (String)SeleniumBaseActionDriver.getTestParamsMap().get(key); 
    return CONFIG.getProperty(key);
  }
  
  public static String getMobileConfigProperty(String key) {
    if (System.getProperty(key) != null) {
      System.out.println("Reading from commandline: " + key + ": " + System.getProperty(key));
      return System.getProperty(key);
    } 
    if (SeleniumBaseActionDriver.getTestParamsMap() != null && SeleniumBaseActionDriver.getTestParamsMap().get(key) != null)
      return (String)SeleniumBaseActionDriver.getTestParamsMap().get(key); 
    return MOBILE_CONFIG.getProperty(key);
  }
  
  public String getXpath(String text) {
    return "//*[contains(text(),\"" + text + "\"" + ")]";
  }
  
  public String getXpath(String text, String htmlTag) {
    return "//" + htmlTag + "[contains(text()," + "\"" + text + "\"" + ")]";
  }
  
  public static synchronized String getTestEnvironmentName() {
    if (ENVIRONMENT_UNDER_TEST == null)
      return TEST_ENV.getProperty("testEnv"); 
    return ENVIRONMENT_UNDER_TEST;
  }
  
  public static String getHomeDirectory() {
    String dir = System.getProperty("user.home");
    return dir;
  }
  
  public void waitInSeconds(long seconds) {
    try {
      Thread.sleep(1000L * seconds);
    } catch (InterruptedException e) {
      log(e);
    } 
  }
  
  public static synchronized boolean isWindows() {
    return (OS.indexOf("win") >= 0);
  }
  
  public static synchronized boolean isUnix() {
    return !(!OS.contains("nix") && !OS.contains("nux") && !OS.contains("aix"));
  }
  
  private void initializeChromeDriverServer() {
    String log = "Initializing chrome driver on ";
    if (isWindows()) {
      log(String.valueOf(log) + "Windows");
      WebDriverManager.chromedriver().setup();
    } else if (isUnix()) {
      log(String.valueOf(log) + "LINUX");
      WebDriverManager.chromedriver().operatingSystem(OperatingSystem.LINUX).setup();
    } else {
      log(String.valueOf(log) + "MAC");
      WebDriverManager.chromedriver().operatingSystem(OperatingSystem.MAC).setup();
    } 
  }
  
  private void initializeFirefoxDriverServer() {
    String log = "Initializing Firefox driver on ";
    if (isWindows()) {
      log(String.valueOf(log) + "Windows");
      WebDriverManager.firefoxdriver().arch64().setup();
    } else if (isUnix()) {
      log(String.valueOf(log) + "LINUX");
      WebDriverManager.firefoxdriver().operatingSystem(OperatingSystem.LINUX).setup();
    } else {
      log(String.valueOf(log) + "MAC");
      WebDriverManager.firefoxdriver().operatingSystem(OperatingSystem.MAC).setup();
    } 
  }
  private void initializeMsEdgeDriverServer() {
	    String log = "Initializing MS edge driver on ";
	    if (isWindows()) {
	      log(String.valueOf(log) + "Windows");
	      WebDriverManager.edgedriver().setup();
	    } else if (isUnix()) {
	      log(String.valueOf(log) + "LINUX");
	      WebDriverManager.edgedriver().operatingSystem(OperatingSystem.LINUX).setup();
	    } else {
	      log(String.valueOf(log) + "MAC");
	      WebDriverManager.edgedriver().operatingSystem(OperatingSystem.MAC).setup();
	    } 
	  }
  
  public void failApiTest(Throwable t, int counter) {
    t.printStackTrace();
    this.test.log(Status.FAIL, "Fail: " + t.getMessage());
  }
  
  public void skipApiTest(Throwable t, int counter) {
    t.printStackTrace();
    this.test.log(Status.SKIP, "Fail: " + t.getMessage());
  }
  
  public String getCurrentDate(String dateFormat) {
    DateFormat df = new SimpleDateFormat(dateFormat);
    Calendar cal = Calendar.getInstance();
    return df.format(cal.getTime());
  }
  
  public <T extends Number> void compareNumericValue(T actual, T expected) throws Exception {
    log("Comparing Numeric Value ");
    log("Expected Text : " + expected);
    log("ACTUAL TEXT IS : " + actual);
    if (actual.doubleValue() != expected.doubleValue())
      throw new Exception("Issue : Actual : " + actual + " but Expected : " + expected); 
  }
  
  public boolean isNumeric(Object val) {
    try {
      Integer.parseInt(val.toString());
      return true;
    } catch (Exception e) {
      return false;
    } 
  }
  
  public String appendUrl(String baseUrl, String... path) {
    String finalUrl = "";
    byte b;
    int i;
    String[] arrayOfString;
    for (i = (arrayOfString = path).length, b = 0; b < i; ) {
      String child = arrayOfString[b];
      finalUrl = String.valueOf(finalUrl) + "/" + child;
      b++;
    } 
    finalUrl = finalUrl.replace("//", "/").replace("///", "/");
    URI uri = URI.create(baseUrl);
    URI uri2 = uri.resolve(finalUrl);
    finalUrl = uri2.toString();
    return finalUrl;
  }
}
