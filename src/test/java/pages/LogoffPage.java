package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object a kijelentkezéshez a Practice Software Testing oldalon.
 *
 * <p><b>Mit modellez:</b> a jobb felső felhasználói menüt (nav-menu) és annak Sign out
 * pontját. Kijelentkezés után az oldal újratölt, és mivel az admin/account oldalak
 * bejelentkezést igényelnek, a login oldalra (/auth/login) kerülünk.
 *
 * <p><b>Funkciói:</b>
 * <ul>
 *   <li>{@link #logoff()} – kijelentkezés a felhasználói menüből</li>
 * </ul>
 *
 * <p>A kattintások a {@link BasePage} segédmetódusán keresztül történnek, így
 * bemutató módban (-Dslowmo=...) lassítva futnak.
 */
public class LogoffPage extends BasePage {

	private By menuButton = By.cssSelector("[data-test='nav-menu']");
	private By logoffButton = By.cssSelector("[data-test='nav-sign-out']");

	/**
	 * @param driver a teszt által elindított WebDriver
	 */
	public LogoffPage(WebDriver driver) {
		super(driver);
	}

	/**
	 * Kijelentkezik a felhasználói menüből. Bejelentkezett felhasználó kell hozzá.
	 */
	public void logoff() {

	    waitAndClick(menuButton);
	    waitAndClick(logoffButton);
	}

}
