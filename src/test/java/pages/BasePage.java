package pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Az összes Page Object közös ősosztálya.
 * 
 * A Page Object Model (POM) mintában minden oldalhoz (vagy funkcióhoz) tartozik egy
 * osztály, amely a lokátorokat és az oldalon végezhető műveleteket tartalmazza.
 * 
 * Ez az osztály azért jött létre, hogy az ismétlődő Selenium-hívások (elem keresése,
 * kattintás, gépelés, választás) egy helyen legyenek, és a leszármazott oldalak
 * ezeket használják.
 * 
 * Bemutató mód (lassítás): a kattintások, gépelések és választások előtt a
 * {@link #pause()} metódus várakozik, hogy a futás szemmel követhető legyen.
 * Alapértéken 0, az indításnál kell beállítani a sebességet.
 */
public class BasePage {

	/**
	 * Bemutató módhoz: ennyi ezredmásodpercet vár minden kattintás, gépelés és
	 * választás előtt. Az értéket a "slowmo" indítási paraméterből olvassa
	 * (pl. -Dslowmo=800); ha nincs megadva, 0, vagyis teljes sebességgel fut.
	 */
	protected static final long SLOW_MO_MS = Long.getLong("slowmo", 0);

	/** A teszt által létrehozott böngésző-példány, amelyet minden oldal közösen használ. */
	protected WebDriver driver;

	/**
	 * @param driver a teszt által elindított WebDriver
	 */
	public BasePage(WebDriver driver) {
		this.driver = driver;
	}

	/**
	 * Várakozik SLOW_MO_MS ideig, hogy a bemutatón követni lehessen a lépéseket.
	 * 
	 * Fontos: ez a várakozás csak a szemnek szól, nem szinkronizálásra való.
	 * Az elemek megjelenésére a tesztek mindenhol explicit várakozással
	 * (WebDriverWait) várnak, ezért a pause() nélkül is megbízhatóan futnak.
	 * Ha a lassítás ki van kapcsolva (0), azonnal visszatér.
	 */
	protected void pause() {
		if (SLOW_MO_MS <= 0) {
			return;
		}
		try {
			Thread.sleep(SLOW_MO_MS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Megkeres egy elemet az oldalon (várakozás nélkül).
	 *
	 * @param locator az elem lokátora
	 * @return a megtalált elem
	 */
	protected WebElement find(By locator) {
		return driver.findElement(locator);

	}

	/**
	 * Rákattint egy elemre. Nem vár az elemre, ezért csak olyan elemre használjuk,
	 * amely biztosan megjelent már az oldalon; egyébként {@link #waitAndClick(By)} kell.
	 *
	 * @param locator az elem lokátora
	 */
	protected void click(By locator) {
		pause();
		find(locator).click();
	}

	/**
	 * Megvárja, hogy az elem kattintható legyen (max. 10 mp), majd rákattint.
	 * Ez a kattintás alapesete: menük, gombok, linkek, amelyek aszinkron jelennek meg.
	 *
	 * @param locator az elem lokátora
	 */
	protected void waitAndClick(By locator) {
		pause();
		new WebDriverWait(driver, Duration.ofSeconds(10))
			.until(ExpectedConditions.elementToBeClickable(locator))
			.click();
	}

	/**
	 * Megvárja, hogy a mező látható legyen (max. 10 mp), majd beleírja a szöveget.
	 *
	 * @param locator a beviteli mező lokátora
	 * @param text a beírandó szöveg
	 */
	protected void type(By locator, String text) {
		pause();
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

	    WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));

	    element.sendKeys(text);
	}

	/**
	 * Legördülő listából (select) választ az opció látható szövege alapján.
	 * Aszinkron töltődő listánál előtte meg kell várni, hogy az opció megjelenjen.
	 *
	 * @param locator a legördülő lista lokátora
	 * @param text a választandó opció szövege
	 */
	protected void selectByText(By locator, String text) {
		pause();
		new Select(find(locator)).selectByVisibleText(text);
	}

	/**
	 * Visszaadja egy elem látható szövegét.
	 *
	 * @param locator az elem lokátora
	 * @return az elem szövege
	 */
	protected String getText(By locator) {
		return find(locator).getText();

	}

	/**
	 * Kiüríti a beviteli mezőt. 
	 * 
	 * @param locator a beviteli mező lokátora
	 */
	protected void clear(By locator) {
		find(locator).clear();

	}

}
