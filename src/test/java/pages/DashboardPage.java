package pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page Object az admin Dashboard oldalhoz (/admin/dashboard).
 *
 * <p><b>Mit modellez:</b> az admin bejelentkezés utáni nyitóoldalt. A betöltést az
 * oldal "Sales over the years" címe alapján ismerjük fel.
 *
 * <p><b>Funkciói:</b>
 * <ul>
 *   <li>{@link #isLoaded()} – ellenőrzi, hogy a Dashboard betöltött-e</li>
 * </ul>
 */
public class DashboardPage extends BasePage {

	private By pageTitle = By.cssSelector("[data-test='page-title']");

	/**
	 * @param driver a teszt által elindított WebDriver
	 */
	public DashboardPage(WebDriver driver) {
		super(driver);
	}

	/**
	 * Ellenőrzi, hogy betöltött-e a Dashboard.
	 *
	 * @return true, ha 10 mp-en belül megjelent a "Sales over the years" cím; különben false
	 */
	public boolean isLoaded() {
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

	    try {
	        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(pageTitle));

	        return title.getText().equals("Sales over the years");

	    } catch (org.openqa.selenium.TimeoutException e) {
	        return false;
	    }
	}

}
