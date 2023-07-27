package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.SessionId;

public abstract class BaseActionDriver 
{
		  protected CommonUtil commonUtil;
		  
		  private String browserName;
		  
		  private String testCaseName;
		  
		  public String getTestCaseName() {
		    return this.testCaseName;
		  }
		  
		  public void setTestCaseName(String testCaseName) {
		    this.testCaseName = testCaseName;
		  }
		  
		  public String getBrowserName() {
		    return this.browserName;
		  }
		  
		  public void setBrowserName(String browserName) {
		    this.browserName = browserName;
		  }
		  
		  public abstract Actions getActionsInstance();
		  
		  public abstract void verifyPageTitle(String paramString);
		  
		  public abstract void enterTestData(By paramBy, Object paramObject);
		  
		  public abstract SessionId getSessionId();
		  
		  public abstract void setImplicitWaitOnDriver(int paramInt);
		  
		  public abstract void initializeLogging();
		  
		  public abstract void handleAlert();
		  
		  public abstract void takeScreenShot(String paramString);
		  
		  public abstract void click(By paramBy);
		  
		  public abstract void click(String paramString);
		  
		  public abstract void click(WebElement paramWebElement);
		  
		  public abstract void clickUsingActionApi(By paramBy);
		  
		  public abstract void clickUsingJavaScript(By paramBy);
		  
		  public abstract void clickUsingJavaScript(WebElement paramWebElement);
		  
		  public abstract void closeBrowser();
		  
		  public abstract WebElement getElement(By paramBy);
		  
		  public abstract List<WebElement> getElements(By paramBy, int paramInt);
		  
		  public abstract <T> WebElement waitForElementToBePresent(T paramT);
		  
		  public abstract <T> WebElement waitForElementToBePresent(T paramT, int paramInt);
		  
		  public abstract <T> WebElement waitForElementToBeClickable(T paramT);
		  
		  public abstract void waitForElementNotPresent(By paramBy, int paramInt);
		  
		  public abstract void type(By paramBy, String paramString);
		  
		  public abstract void typeUsingActionApi(By paramBy, String paramString);
		  
		  public abstract void clearTextBoxValue(By paramBy);
		  
		  public abstract <T> WebElement waitForElementToBeVisible(T paramT);
		  
		  public abstract <T> WebElement waitForElementToBeVisible(T paramT, int paramInt);
		  
		  public abstract void waitForElementToBeEnabled(By paramBy, int paramInt);
		  
		  public abstract boolean isElementPresent(By paramBy, int paramInt);
		  
		  public abstract void deletecookies();
		  
		  public abstract String getInputBoxValue(By paramBy);
		  
		  public abstract String getText(By paramBy);
		  
		  public abstract boolean isElementVisible(By paramBy, int paramInt);
		  
		  public abstract Object executeJavaScript(String paramString);
		  
		  public abstract String executeJavaScript(String paramString, WebElement paramWebElement);
		  
		  public abstract void refreshBrowser();
		  
		  public abstract WebDriver.Options manage();
		  
		  public abstract void close();
		  
		  public abstract void quit();
		  
		  public abstract WebDriver.Navigation navigate();
		  
		  public abstract String getTitle();
		  
		  public abstract Capabilities getCapabilities();
		  
		  public abstract String getCurrentUrl();
		  
		  public abstract void get(String paramString);
		  
		  public abstract WebDriver.TargetLocator switchTo();
		  
		  public abstract void sendKeys(By paramBy, Object paramObject);
		  
		  public abstract boolean isDriverSessionActive();
		  
		  public abstract void verifyUrl(String paramString, int paramInt);
		  
		  public abstract void mouseHover(By paramBy);
		  
		  public abstract WebElement scrollDownTo(By paramBy);
		  
		  public abstract Set<String> getWindowHandles();
		  
		  public abstract String getWindowHandle();
		  
		  public abstract void selectByText(By paramBy, String paramString);
		  
		  public abstract void refreshBrowserUntilElementPresent(By paramBy, int paramInt);
		  
		  public abstract void typeWithDelay(By paramBy, String paramString, long paramLong);
		  
		  public List<String> switchToTabWindow() {
		    boolean flag = true;
		    Set<String> handles = null;
		    for (int i = 0; i < 7; i++) {
		      handles = getWindowHandles();
		      if (handles.size() == 2) {
		        flag = false;
		        break;
		      } 
		      this.commonUtil.log("Waiting for new tab to open..");
		      this.commonUtil.waitInSeconds(5L);
		    } 
		    if (flag)
		      throw new WebDriverException("Tab window is not opened"); 
		    this.commonUtil.log("Moving ahead on new tab");
		    String mainWindow = getWindowHandle();
		    Object[] hndlArr = handles.toArray();
		    String valComp = (String)hndlArr[0];
		    String tabWindow = null;
		    if (valComp.equalsIgnoreCase(mainWindow)) {
		      tabWindow = (String)hndlArr[1];
		    } else {
		      tabWindow = valComp;
		    } 
		    close();
		    switchTo().window(tabWindow);
		    List<String> windowsId = new ArrayList<>();
		    windowsId.add(mainWindow);
		    windowsId.add(tabWindow);
		    return windowsId;
		  }
		  
		  public List<String> switchToTabWithoutClosingMainWindow() {
		    boolean flag = true;
		    Set<String> handles = null;
		    for (int i = 0; i <= 3; i++) {
		      handles = getWindowHandles();
		      if (handles.size() == 2) {
		        flag = false;
		        break;
		      } 
		      this.commonUtil.log("Waiting for new tab to open..");
		      this.commonUtil.waitInSeconds(3L);
		    } 
		    if (flag)
		      throw new WebDriverException("Tab window is not opened"); 
		    this.commonUtil.log("Moving ahead on new tab");
		    String mainWindow = getWindowHandle();
		    Object[] hndlArr = handles.toArray();
		    String valComp = (String)hndlArr[0];
		    String tabWindow = null;
		    if (valComp.equalsIgnoreCase(mainWindow)) {
		      tabWindow = (String)hndlArr[1];
		    } else {
		      tabWindow = valComp;
		    } 
		    switchTo().window(tabWindow);
		    List<String> windowsId = new ArrayList<>();
		    windowsId.add(mainWindow);
		    windowsId.add(tabWindow);
		    return windowsId;
		  }
		  
		  public String getAbsoluteXPath(WebElement element) {
		    return executeJavaScript("function absoluteXPath(element) {var comp, comps = [];var parent = null;var xpath = '';var getPos = function(element) {var position = 1, curNode;if (element.nodeType == Node.ATTRIBUTE_NODE) {return null;}for (curNode = element.previousSibling; curNode; curNode = curNode.previousSibling){if (curNode.nodeName == element.nodeName) {++position;}}return position;};if (element instanceof Document) {return '/';}for (; element && !(element instanceof Document); element = element.nodeType ==Node.ATTRIBUTE_NODE ? element.ownerElement : element.parentNode) {comp = comps[comps.length] = {};switch (element.nodeType) {case Node.TEXT_NODE:comp.name = 'text()';break;case Node.ATTRIBUTE_NODE:comp.name = '@' + element.nodeName;break;case Node.PROCESSING_INSTRUCTION_NODE:comp.name = 'processing-instruction()';break;case Node.COMMENT_NODE:comp.name = 'comment()';break;case Node.ELEMENT_NODE:comp.name = element.nodeName;break;}comp.position = getPos(element);}for (var i = comps.length - 1; i >= 0; i--) {comp = comps[i];xpath += '/' + comp.name.toLowerCase();if (comp.position !== null) {xpath += '[' + comp.position + ']';}}return xpath;} return absoluteXPath(arguments[0]);", element);
		  }
}
