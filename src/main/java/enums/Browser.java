package enums;

public enum Browser {
	  FIREFOX("Firefox"),
	  CHROME("Chrome"),
	  MSEDGE("msedge"),
	  SAFARI("Safari"),
	  IOS_SAFARI("iosSafari"),
	  ANDROID_CHROME("androidChrome"),
	  ANDROID_NATIVE_APP("AndroidApp"),
	  IOS_NATIVE_APP("IOSApp");
	  
	  String browserName;
	  
	  Browser(String browserName) {
	    this.browserName = browserName;
	  }
	  
	  public String toString() {
	    return this.browserName;
	  }
}
