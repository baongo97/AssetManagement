package util;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class WebDriverConnector {
	private static WebDriver driver;
	
	public static WebDriver getDriver() {
		if (driver == null) {
			try {
		        System.setProperty("webdriver.chrome.driver", "C:\\Program Files\\chromedriver-win64\\chromedriver.exe");
		        
		        // Configure Chrome to run in headless mode
		        ChromeOptions options = new ChromeOptions();
		        options.addArguments("--headless"); // Run in headless mode
		        options.addArguments("--disable-gpu"); // Disable GPU rendering (optional for stability)
		        options.addArguments("--window-size=1920,1080"); // Optional: set a standard screen size for rendering
		        
		        // Initialize WebDriver
		        driver = new ChromeDriver(options);   
	            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
	                quitDriver();
	            }));
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		return driver;
	}
	public static void quitDriver() {
		if (driver != null) {
			driver.quit();
			driver = null;
		}
	}
	public static void main(String[] args) {
		WebDriver driver = WebDriverConnector.getDriver();
		driver.get("https://cafef.vn/du-lieu/hose/hpg.chn");
        List<WebElement> symbols = driver.findElements(By.id("symbolbox"));

        // Iterate through the elements and print the extracted data
        for (WebElement symbol : symbols) {
            System.out.println(symbol.getText());
        }
        driver.get("https://cafef.vn/du-lieu/hose/hsg.chn");
        symbols = driver.findElements(By.id("symbolbox"));
        for (WebElement symbol : symbols) {
            System.out.println(symbol.getText());
        }
	}
}
