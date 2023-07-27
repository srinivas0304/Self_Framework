package retry;

import utils.CommonUtil;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.testng.Reporter;

public class RetryAnalyzer implements IRetryAnalyzer {
	  private final ThreadLocal<Integer> retryCount = new ThreadLocal<Integer>() {
	      public Integer initialValue() {
	        return Integer.valueOf(0);
	      }
	    };
	  
	  private ThreadLocal<Integer> retryMaxCount = new ThreadLocal<>();
	  
	  private ThreadLocal<String> testName = new ThreadLocal<>();
	  
	  private static int RETRY_MAXATTEMPT;
	  
	  public synchronized boolean retry(ITestResult result) {
	    this.testName.set(result.getName());
	    this.retryMaxCount.set(Integer.valueOf(getMaxAttempt()));
	    if (!result.isSuccess()) {
	      if (((Integer)this.retryCount.get()).intValue() < ((Integer)this.retryMaxCount.get()).intValue()) {
	        Reporter.log("running retry logic for  '" + result.getName(), true);
	        this.retryCount.set(Integer.valueOf(((Integer)this.retryCount.get()).intValue() + 1));
	        result.setStatus(2);
	        return true;
	      } 
	      result.setStatus(2);
	    } else {
	      result.setStatus(1);
	    } 
	    return false;
	  }
	  
	  private static int getMaxAttempt() {
	    if (RETRY_MAXATTEMPT == 0) {
	      String retryAttempt = CommonUtil.getConfigProperty("retry_attempt");
	      if (retryAttempt != null && retryAttempt.length() > 0) {
	        RETRY_MAXATTEMPT = Integer.parseInt(retryAttempt);
	      } else {
	        RETRY_MAXATTEMPT = 1;
	      } 
	    } 
	    return RETRY_MAXATTEMPT;
	  }
}