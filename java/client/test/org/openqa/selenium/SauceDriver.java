package org.openqa.selenium;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

public class SauceDriver extends RemoteWebDriver {
  private static final String SELENIUM_VERSION_ENV_NAME = "SAUCE_SELENIUM_VERSION";
  private static final String SAUCE_APIKEY_ENV_NAME = "SAUCE_APIKEY";
  private static final String SAUCE_USERNAME_ENV_NAME = "SAUCE_USERNAME";
  private static final String DESIRED_BROWSER_VERSION_ENV_NAME = "SAUCE_BROWSER_VERSION";
  
  // Should be one of the values listed for Platform, e.g. xp, win7, android, ...
  private static final String DESIRED_OS_ENV_NAME = "SAUCE_OS";

  public SauceDriver(DesiredCapabilities desiredCapabilities) {
    super(getSauceEndpoint(),
      munge(
        desiredCapabilities,
        getSeleniumVersion(),
        getDesiredBrowserVersion(),
        getDesiredOS()));
  }

  private static String getDesiredBrowserVersion() {
    return getNonNullEnv(DESIRED_BROWSER_VERSION_ENV_NAME);
  }

  private static String getDesiredOS() {
    return getNonNullEnv(DESIRED_OS_ENV_NAME);
  }

  private static String getSeleniumVersion() {
    return getNonNullEnv(SELENIUM_VERSION_ENV_NAME);
  }
  
  private static String getNonNullEnv(String propertyName) {
    String value = System.getenv(propertyName);
    Preconditions.checkNotNull(value);
    return value;
  }

  private static URL getSauceEndpoint() {
    String sauceUsername = getNonNullEnv(SAUCE_USERNAME_ENV_NAME);
    String sauceKey = getNonNullEnv(SAUCE_APIKEY_ENV_NAME);

    try {
      return new URL(String.format("http://%s:%s@ondemand.saucelabs.com:80/wd/hub", sauceUsername, sauceKey));
    } catch (MalformedURLException e) {
      Throwables.propagate(e);
    }
    throw new IllegalStateException("Should have returned or thrown");
  }

  private static Capabilities munge(DesiredCapabilities desiredCapabilities, String seleniumVersion, String browserVersion, String os) {
    DesiredCapabilities mungedCapabilities = new DesiredCapabilities(desiredCapabilities);
    mungedCapabilities.setCapability("selenium-version", seleniumVersion);
    mungedCapabilities.setVersion(browserVersion);
    mungedCapabilities.setPlatform(Platform.extractFromSysProperty(os));
    return mungedCapabilities;
  }
}