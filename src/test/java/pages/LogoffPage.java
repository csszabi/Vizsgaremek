package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object a kijelentkezéshez a Practice Software Testing oldalon.
 * 
 * Lefedett folyamat: a jobb felső felhasználói menü (nav-menu) → Sign out.
 * Kijelentkezés után az oldal újratölt, és mivel az admin/account oldalak
 * bejelentkezést igényelnek, a login oldalra (/auth/login) kerülünk.
 * 
 * A kattintások a {@link BasePage} segédmetódusán keresztül történnek, így
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
