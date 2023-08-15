package zapUtil;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.zaproxy.clientapi.core.ApiResponse;
import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.clientapi.core.ClientApiException;
import io.github.bonigarcia.wdm.WebDriverManager;

public class ZapSecurityTest
{
	static final String ZAP_PROXY_ADDRESS	=   "localhost";
	static final int ZAP_PROXY_PORT			=	8080;
	static final String ZAP_API_KEY 		=   "d1iir2hiq3leubqunoalqjoqe4";
	
	private WebDriver driver;
	private ClientApi api;
	
	@BeforeMethod
	public void setup()
	{
		String proxyServerUrl = ZAP_PROXY_ADDRESS +":"+ZAP_PROXY_PORT;
		Proxy proxy = new Proxy();
		proxy.setHttpProxy(proxyServerUrl);
		proxy.setSslProxy(proxyServerUrl);
		
		ChromeOptions co = new ChromeOptions();
		co.setAcceptInsecureCerts(true);
		co.setProxy(proxy);
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver(co);
		api = new ClientApi(ZAP_PROXY_ADDRESS, ZAP_PROXY_PORT,ZAP_API_KEY);
	}
	
	@Test
	public void rgtSecurityTest()
	{
		driver.get("https://www.ratnaglobaltech.com/");
		Assert.assertTrue(driver.getTitle().contains("RGT - IT Services | Salesforce Solutions | QA Solutions | Ratna Global Technologies"));
	}
	
	@AfterMethod
	public void tearDown()
	{
		if(api!=null)
		{
			String title = "RGT ZAP Security Test";
			String template = "traditional-html";
			String description = "This is RGT zap security test report";
			String reportfilename = "rgt-zap-report.html";
			String targetfolder = System.getProperty("user.dir");
			try {
				ApiResponse response= api.reports.generate(title, template, null, description, null, null, null, 
						null, null, reportfilename, null, targetfolder, null);
				System.out.println("ZAP report generated at this location: "+response.toString());
			} catch (ClientApiException e) {
				e.printStackTrace();
			}
		}
		driver.quit();
	}
}
