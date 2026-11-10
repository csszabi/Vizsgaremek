package pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page Object a vásárlói fiók nyitóoldalához (/account).
 *
 * <p><b>Mit modellez:</b> a vásárlói bejelentkezés utáni nyitóoldalt. A betöltést az
 * oldal "My account" címe alapján ismerjük fel.
 *
 * <p><b>Funkciói:</b>
 * <ul>
 *   <li>{@link #isLoaded()} – ellenőrzi, hogy a My account oldal betöltött-e</li>
 * </ul>
 */
public class AccountPage extends BasePage {

	private By pageTitle = By.cssSelector("[data-test='page-title']");

	/**
	 * @param driver a teszt által elindított WebDriver
	 */
	public AccountPage(WebDriver driver) {
		super(driver);
	}

	/**
	 * Ellenőrzi, hogy betöltött-e a vásárlói fiók oldala.
	 *
	 * @return true, ha 10 mp-en belül megjelent a "My account" cím; különben false
	 */
	public boolean isLoaded() {
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	    try {
	        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(pageTitle));
	        return title.getText().equals("My account");
	    } catch (org.openqa.selenium.TimeoutException e) {
	        return false;
	    }
	}
}