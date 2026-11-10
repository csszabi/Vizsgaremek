package pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page Object az adatkezelési nyilatkozathoz (Privacy Policy) a Practice Software Testing oldalon.
 *
 * <p><b>Mit modellez:</b> a lábléc Privacy Policy linkjét és az általa megnyíló
 * /privacy oldalt. Bejelentkezés nem kell hozzá.
 *
 * <p><b>Funkciói:</b>
 * <ul>
 *   <li>{@link #openFromFooter()} – a nyilatkozat megnyitása a lábléc linkjéről</li>
 *   <li>{@link #getHeading()} – a nyilatkozat főcíme</li>
 *   <li>{@link #getContent()} – a nyilatkozat teljes szövege</li>
 * </ul>
 *
 * <p>A kattintások a {@link BasePage} segédmetódusán keresztül történnek, így
 * bemutató módban (-Dslowmo=...) lassítva futnak.
 */
public class PrivacyPolicyPage extends BasePage {

	/** Explicit várakozás (max. 10 mp) az oldalállapotokhoz. */
	private WebDriverWait wait;

	// A lábléc linkjének nincs data-test attribútuma, ezért a cél címe alapján keressük
	private By privacyLink = By.cssSelector("a[href='/privacy']");

	// A nyilatkozat főcíme ("Privacy Policy for Toolshop")
	private By heading = By.tagName("h1");

	// Az oldal törzse, a nyilatkozat szövegének ellenőrzéséhez
	private By body = By.tagName("body");

	/**
	 * @param driver a teszt által elindított WebDriver
	 */
	public PrivacyPolicyPage(WebDriver driver) {
		super(driver);
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	}

	/**
	 * A lábléc Privacy Policy linkjére kattint, és megvárja, hogy betöltsön a
	 * nyilatkozat oldala (/privacy) a főcímmel együtt.
	 */
	public void openFromFooter() {

		// A lábléc az oldal alján van; a waitAndClick a kattinthatóságot várja meg,
		// a Selenium a kattintás előtt magától odagörget
		waitAndClick(privacyLink);

		// Az Angular oldalváltás nem tölti újra az oldalt, ezért az URL-re, a böngésző
		// címsorára és a főcímre várunk (a címsort az Angular az oldalváltás után állítja át)
		wait.until(ExpectedConditions.urlContains("/privacy"));
		wait.until(ExpectedConditions.titleContains("Privacy Policy"));
		wait.until(ExpectedConditions.visibilityOfElementLocated(heading));
	}

	/**
	 * @return a nyilatkozat főcíme
	 */
	public String getHeading() {
		return getText(heading);
	}

	/**
	 * @return az oldal teljes látható szövege (benne a nyilatkozat)
	 */
	public String getContent() {
		return getText(body);
	}
}
