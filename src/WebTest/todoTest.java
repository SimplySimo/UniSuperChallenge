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
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
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

	/*
	 * Parameters for Browsers wished to be used in Parameterised testing.
	 */
	@Parameterized.Parameters
	public static LinkedList<String[]> getEnvironments() throws Exception {
		LinkedList<String[]> env = new LinkedList<String[]>();
		env.add(new String[] { Platform.WINDOWS.toString(), "chrome", "latest" });
		env.add(new String[] { Platform.WINDOWS.toString(), "firefox", "latest" });

		return env;
	}

	/*
	 * Set up browser variables for current thread.
	 */
	public todoTest(String platform, String browserName, String browserVersion) {
		this.platform = platform;
		this.browserName = browserName;
		this.browserVersion = browserVersion;
	}

	// initialise web driver.
	private WebDriver driver;

	/*
	 * Set the capabilities and select the correct web driver for current
	 * thread.
	 */
	@Before
	public final void setUp() throws Exception {

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
		} else {
			System.out.println("ERROR unable to located settings for browser selected");
		}

	}

	/*
	 * simple test which covers all required actions.
	 */
	@Test
	public final void testSimple() throws Exception {

		WebDriverWait wait = new WebDriverWait(driver, 15);
		Actions action = new Actions(driver);

		// Navigate to the starting website.
		driver.get("http://todomvc.com");
		assertEquals("TodoMVC", driver.getTitle());

		// find the correct page to navigate to and click on link.
		driver.findElement(By.cssSelector("a[href=\"examples/angularjs\"]")).click();
		waitForPageLoaded();
		assertEquals("http://todomvc.com/examples/angularjs/#/", this.driver.getCurrentUrl());

		//add new item to list.
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[@id='todo-form']/input")));
		driver.findElement(By.xpath("//form[@id='todo-form']/input")).sendKeys("hella" + Keys.ENTER);
		//wait till item is in list and verify.
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ul[@id='todo-list']/li/div/label")));
		assertEquals("hella", driver.findElement(By.id("todo-list"))
				.findElement(By.xpath("//ul[@id='todo-list']/li[1]/div/label")).getText());

		Thread.sleep(100);

		// edit item in list, try catch due to issue in firefox.
		try {
			WebElement thing = driver.findElement(By.xpath("//ul[@id='todo-list']/li[1]/div/label"));
			action.doubleClick(thing).build().perform();
			Thread.sleep(100);
			action.sendKeys(Keys.BACK_SPACE + "o" + Keys.ENTER).build().perform();

		} catch (Exception e) {
			System.out.println("Edit item failed in browser:" + browserName);
		}

		Thread.sleep(100);

		// complete the first To-do item by ticking the checkbox.
		driver.findElement(By.id("todo-list")).findElement(By.xpath("//input[@type='checkbox'][1]")).click();
		assertTrue(driver.findElement(By.id("todo-list")).findElement(By.xpath("//input[@type='checkbox'][1]"))
				.isSelected());

		Thread.sleep(100);

		// re-activate the completed To-do item.
		driver.findElement(By.id("todo-list")).findElement(By.xpath("//input[@type='checkbox'][1]")).click();
		assertFalse(driver.findElement(By.id("todo-list")).findElement(By.xpath("//input[@type='checkbox'][1]"))
				.isSelected());

		Thread.sleep(100);

		// add a second To-do item to the list.
		driver.findElement(By.id("new-todo")).sendKeys("TEST" + Keys.ENTER);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ul[@id='todo-list']/li[2]/div/label")));
		assertEquals("TEST", driver.findElement(By.id("todo-list"))
				.findElement(By.xpath("//ul[@id='todo-list']/li[2]/div/label")).getText());

		Thread.sleep(100);

		// complete all active To-dos by clicking the down arrow at the top-left
		// of the UI.
		driver.findElement(By.id("toggle-all")).click();
		assertTrue(driver.findElement(By.id("toggle-all")).isSelected());

		Thread.sleep(100);

		// filter the visible To-dos by Completed state.
		driver.findElement(By.id("filters")).findElement(By.linkText("Completed")).click();
		;
		assertEquals("http://todomvc.com/examples/angularjs/#/completed", this.driver.getCurrentUrl());

		Thread.sleep(100);

		// clear a single To-do item from the list completely by clicking the remove button.
		WebElement tmpElement = driver.findElement(By.xpath("//ul[@id='todo-list']/li/div/button"));
		JavascriptExecutor executor = (JavascriptExecutor) driver;
		String js = "arguments[0].style.height='auto'; arguments[0].style.visibility='visible';";
		executor.executeScript(js, tmpElement);
		executor.executeScript("arguments[0].click();", tmpElement);
		Thread.sleep(100);

		// clear all completed To-do items from the list completely.
		driver.findElement(By.id("clear-completed")).click();
		List<WebElement> deleteLinks = driver.findElements(By.xpath("//ul[@id='todo-list']/li"));
		assertTrue(deleteLinks.isEmpty());
		Thread.sleep(1000);

		System.out.println("All tests besides listed ones above passed in Browser: " + this.browserName);

	}
	
	//close browser pages.
	@After
	public final void tearDown() throws Exception {
		driver.quit();
	}

	//used to ensure the page has completely loaded before continuing.
	private final void waitForPageLoaded() {
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
