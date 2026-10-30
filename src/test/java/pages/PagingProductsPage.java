package pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page Object a kezdőoldali terméklista lapozójához.
 *
 * <p><b>Mit modellez:</b> a terméklista alatti lapozót.Bejelentkezés nem kell hozzá.
 *
 * <p><b>Funkciói:</b>
 * <ul>
 *   <li>{@link #open()} – a kezdőoldal megnyitása a lapozó megvárásával</li>
 *   <li>{@link #goToPage(int)} – lapozás a megadott oldalra</li>
 *   <li>{@link #isPageActive(int)} – ellenőrzi, hogy a megadott oldal az aktív-e</li>
 * </ul>
 *
 * <p>A kattintások a {@link BasePage} segédmetódusán keresztül történnek, így
 * bemutató módban (-Dslowmo=...) lassítva futnak.
 */
public class PagingProductsPage extends BasePage {

	/** Explicit várakozás (max. 10 mp) az oldalállapotokhoz. */
	private WebDriverWait wait;

	/** Az adott oldalszám gombja a lapozóban. */
	private By pageButton(int pageNumber) {
        return By.cssSelector("button[aria-label='Page-" + pageNumber + "']");
    }

	/** Az adott oldalszám gombja, de csak akkor, ha az az aktív oldal. */
	private By activePageButton(int pageNumber) {
		// //li[contains(@class, 'active')]   : a lapozó azon eleme, amelyik épp aktív (az aktuális oldal),
		// /button[@aria-label='Page-N']      : és közvetlenül benne az N. oldal gombja. Csak akkor talál elemet, ha éppen az N. oldal az aktív.
		return By.xpath("//li[contains(@class, 'active')]/button[@aria-label='Page-" + pageNumber + "']");
	}

	/**
	 * @param driver a teszt által elindított WebDriver
	 */
    public PagingProductsPage(WebDriver driver) {
		super(driver);
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /** Megnyitja a kezdőoldalt, és megvárja, hogy megjelenjen a lapozó. */
    public void open() {
        driver.get("https://practicesoftwaretesting.com/");

        wait.until(ExpectedConditions.elementToBeClickable(pageButton(1)));
    }

    /**
     * Rákattint az adott oldalszámra, és megvárja, hogy az legyen az aktív oldal.
     *
     * @param pageNumber a megnyitandó oldal száma
     */
    public void goToPage(int pageNumber) {

        waitAndClick(pageButton(pageNumber));

        // A termékek aszinkron töltődnek; a következő kattintás előtt meg kell
        // várni, hogy a lapozó átváltson
        wait.until(ExpectedConditions.presenceOfElementLocated(activePageButton(pageNumber)));
    }

    /**
     * Ellenőrzi, hogy az adott oldal-e az aktív.
     *
     * @param pageNumber az ellenőrizendő oldalszám
     * @return true, ha 10 mp-en belül ez az oldal aktív; különben false
     */
    public boolean isPageActive(int pageNumber) {
    	try {
    		return wait.until(ExpectedConditions.presenceOfElementLocated(activePageButton(pageNumber))) != null;

    	} catch (org.openqa.selenium.TimeoutException e) {
    		return false;
    	}
    }
}
