package utils;

public class CommonHelper 
{
	public static String getUrl() 
	{
		
		return CommonUtil.getConfigProperty(ConfigKeys.URL_ACTITIME);
	}
}
