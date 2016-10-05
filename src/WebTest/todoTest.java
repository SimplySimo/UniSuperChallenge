package WebTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@RunWith(Parallelized.class)
public class todoTest {
	private String platform;
	private String browserName;
	private String browserVersion;

	@Parameterized.Parameters
	public static LinkedList<String[]> getEnvironments() throws Exception {
		LinkedList<String[]> env = new LinkedList<String[]>();
		env.add(new String[] { Platform.WINDOWS.toString(), "chrome", "latest" });
		env.add(new String[] { Platform.WINDOWS.toString(), "firefox", "latest" });

		return env;
	}

	public todoTest(String platform, String browserName, String browserVersion) {
		this.platform = platform;
		this.browserName = browserName;
		this.browserVersion = browserVersion;
	}

	private WebDriver driver;

	@Before
	public void setUp() throws Exception {
		DesiredCapabilities capability = new DesiredCapabilities();
		capability.setCapability("platform", platform);
		capability.setCapability("browser", browserName);
		capability.setCapability("browserVersion", browserVersion);
		capability.setCapability("name", "Parallel test");
		if (browserName == "firefox") {
			System.setProperty("webdriver.gecko.driver", "geckodriver.exe");
			driver = new FirefoxDriver();
		} else if (browserName == "chrome") {
			System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
			driver = new ChromeDriver();
		}

		else if (browserName == "edge") {
			System.setProperty("webdriver.edge.driver", "MicrosoftWebDriver.exe");
			driver = new EdgeDriver();
		} else {
			System.setProperty("webdriver.gecko.driver", "geckodriver.exe");
			driver = new FirefoxDriver();
		}

	}

	@Test
	public void testSimple() throws Exception {
		WebDriverWait wait = new WebDriverWait(driver, 15);

		Actions action = new Actions(driver);
		// Navigate to Website
		driver.get("http://todomvc.com");
		assertEquals("TodoMVC", driver.getTitle());

		// find the correct page to navigate to
		driver.findElement(By.cssSelector("a[href=\"examples/angularjs\"]")).click();
		waitForPageLoaded();
		assertEquals("http://todomvc.com/examples/angularjs/#/", this.driver.getCurrentUrl());

		// add new item
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[@id='todo-form']/input")));
		driver.findElement(By.xpath("//form[@id='todo-form']/input")).sendKeys("hella" + Keys.ENTER);

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ul[@id='todo-list']/li/div/label")));
		assertEquals("hella", driver.findElement(By.id("todo-list"))
				.findElement(By.xpath("//ul[@id='todo-list']/li[1]/div/label")).getText());

		Thread.sleep(100);

		// edit item
		try {
			WebElement thing = driver.findElement(By.xpath("//ul[@id='todo-list']/li[1]/div/label"));
			action.doubleClick(thing).build().perform();
			Thread.sleep(100);
			action.sendKeys(Keys.BACK_SPACE + "o" + Keys.ENTER).build().perform();

		} catch (Exception e) {
			System.out.println("Edit item failed in browser:" + browserName);
		}

		Thread.sleep(100);

		// complete To-do item
		driver.findElement(By.id("todo-list")).findElement(By.xpath("//input[@type='checkbox'][1]")).click();
		assertTrue(driver.findElement(By.id("todo-list")).findElement(By.xpath("//input[@type='checkbox'][1]"))
				.isSelected());

		Thread.sleep(100);

		// re-activate a completed To-do item
		driver.findElement(By.id("todo-list")).findElement(By.xpath("//input[@type='checkbox'][1]")).click();
		assertFalse(driver.findElement(By.id("todo-list")).findElement(By.xpath("//input[@type='checkbox'][1]"))
				.isSelected());

		Thread.sleep(100);

		// add a second To-do
		driver.findElement(By.id("new-todo")).sendKeys("TEST" + Keys.ENTER);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ul[@id='todo-list']/li[2]/div/label")));
		assertEquals("TEST", driver.findElement(By.id("todo-list"))
				.findElement(By.xpath("//ul[@id='todo-list']/li[2]/div/label")).getText());

		Thread.sleep(100);

		// complete all active To-dos by clicking the down arrow at the top-left
		// of the UI
		driver.findElement(By.id("toggle-all")).click();
		assertTrue(driver.findElement(By.id("toggle-all")).isSelected());

		Thread.sleep(100);

		// filter the visible To-dos by Completed state
		driver.findElement(By.id("filters")).findElement(By.linkText("Completed")).click();
		;
		assertEquals("http://todomvc.com/examples/angularjs/#/completed", this.driver.getCurrentUrl());

		Thread.sleep(100);

		// clear a single To-do item from the list completely by clicking the
		WebElement tmpElement = driver.findElement(By.xpath("//ul[@id='todo-list']/li/div/button"));
		JavascriptExecutor executor = (JavascriptExecutor) driver;
		String js = "arguments[0].style.height='auto'; arguments[0].style.visibility='visible';";
		executor.executeScript(js, tmpElement);
		executor.executeScript("arguments[0].click();", tmpElement);
		Thread.sleep(100);

		// clear all completed To-do items from the list completely
		driver.findElement(By.id("clear-completed")).click();
		List<WebElement> deleteLinks = driver.findElements(By.xpath("//ul[@id='todo-list']/li"));
		assertTrue(deleteLinks.isEmpty());
		Thread.sleep(1000);

		System.out.println("All besides listed tests above passed in Browser: " + this.browserName);

	}

	@After
	public void tearDown() throws Exception {
		driver.quit();
	}

	public void waitForPageLoaded() {
		ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				return ((JavascriptExecutor) driver).executeScript("return document.readyState").toString()
						.equals("complete");
			}
		};
		try {
			Thread.sleep(1000);
			WebDriverWait wait = new WebDriverWait(driver, 30);
			wait.until(expectation);
		} catch (Throwable error) {
			Assert.fail("Timeout waiting for Page Load Request to complete.");
		}
	}

}
