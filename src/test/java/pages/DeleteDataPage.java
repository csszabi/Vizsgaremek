package pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page Object egy kategória törléséhez a Practice Software Testing admin felületén.
 *
 * <p><b>Mit modellez:</b> a kategórialista Delete funkcióját. Menü → Categories →
 * a kategória sorában Delete → "Category deleted." toast üzenet → a sor eltűnik a listából.
 *
 * <p><b>Funkciói:</b>
 * <ul>
 *   <li>{@link #open()} – a kezdőoldal megnyitása</li>
 *   <li>{@link #deleteData(String, String, String)} – a teljes folyamat bejelentkezéssel együtt</li>
 *   <li>{@link #deleteCategory(String)} – egy kategória törlése (már bejelentkezett adminnal)</li>
 *   <li>{@link #isDeleteSuccessful()} – a "Category deleted." üzenet ellenőrzése</li>
 *   <li>{@link #isDeleted(String)} – ellenőrzi, hogy a sor eltűnt-e a listából</li>
 * </ul>
 *
 * <p><b>Megjegyzés:</b> az oldal nem kér megerősítést törlés előtt. Olyan kategória,
 * amelyhez termék tartozik, nem törölhető (a szerver hibát ad), ezért a teszt mindig
 * saját, frissen létrehozott kategóriát töröl.
 *
 * <p>A kattintások, gépelések és választások a {@link BasePage} segédmetódusain
 * keresztül történnek, így bemutató módban (-Dslowmo=...) lassítva futnak.
 */
public class DeleteDataPage extends BasePage {

	// Bejelentkezés
	private By signInButton = By.cssSelector("[data-test='nav-sign-in']");
	private By emailAddressField = By.id("email");
	private By passwordField = By.id("password");
	private By loginButton = By.cssSelector("[data-test='login-submit']");

	// Navigáció az admin menüben
	private By menuButton = By.cssSelector("[data-test='nav-menu']");
	private By categoriesButton = By.cssSelector("[data-test='nav-admin-categories']");

	// A jobb felső sarokban felugró toast üzenet (kb. 5 mp-ig látszik)
	private By successMessage = By.cssSelector("[role='alert'].toast-message");

	/**
	 * A lista azon sora, amelynek valamelyik cellája (pl. a Name vagy a Slug oszlop)
	 * pontosan a megadott szöveg.
	 */
	private By categoryRow(String categoryName) {
		// //tr[...]          : az a táblázatsor,
	    // .//td[...]         : amelyen belül van olyan cella,
	    // normalize-space()  : aminek a szövege (szélső szóközök nélkül) pontosan categoryName
	    return By.xpath(
	        "//tr[.//td[normalize-space()='" + categoryName + "']]"
	    );
	}

	/** Az adott sor Delete gombja (data-test="category-delete-{id}"). */
	private By deleteButton(String categoryName) {
		// //tr[...]                          : az a táblázatsor,
	    // .//td[normalize-space()='...']     : amelyben van egy cella, aminek a szövege pontosan categoryName,
	    // //button[contains(@data-test, ...)]: és azon a soron belül a Delete gomb (a data-test értéke "category-delete-{id}", ezért elég az elejére illeszteni)
	    return By.xpath(
	        "//tr[.//td[normalize-space()='" + categoryName + "']]"
	        + "//button[contains(@data-test, 'category-delete-')]"
	    );
	}

	/** Explicit várakozás (max. 10 mp) az ellenőrzésekhez és az oldalállapotokhoz. */
	private WebDriverWait wait;

	/**
	 * @param driver a teszt által elindított WebDriver
	 */
	public DeleteDataPage(WebDriver driver) {
		super(driver);
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	}

	/** Megnyitja az oldal kezdőlapját. */
	public void open() {

		driver.get("https://practicesoftwaretesting.com/");
	}

	/**
	 * A teljes folyamat bejelentkezéssel együtt: a kezdőoldalról,
	 * kijelentkezett állapotból kell indítani.
	 *
	 * @param email admin e-mail címe
	 * @param password admin jelszava
	 * @param categoryName a törlendő kategória neve vagy slugja (ez alapján keresi a sort)
	 */
	public void deleteData(String email, String password, String categoryName) {

		waitAndClick(signInButton);

		type(emailAddressField, email);
		type(passwordField, password);
		waitAndClick(loginButton);

		// Megvárjuk a bejelentkezés utáni átirányítást (teljes oldal-újratöltés)
		wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/auth/login")));

		deleteCategory(categoryName);
	}

	/**
	 * Töröl egy kategóriát. Már bejelentkezett admin kell hozzá (bármelyik admin
	 * oldalról indítható, mert a menüből nyitja meg a Categories listát).
	 * Az eredményt az {@link #isDeleteSuccessful()} és az {@link #isDeleted(String)}
	 * metódusokkal lehet ellenőrizni.
	 *
	 * @param categoryName a törlendő kategória neve vagy slugja (ez alapján keresi a sort)
	 */
	public void deleteCategory(String categoryName) {

		// Kategóriák oldal megnyitása
		waitAndClick(menuButton);
		waitAndClick(categoriesButton);

		// A törlendő kategória sorának megvárása
		wait.until(ExpectedConditions.visibilityOfElementLocated(categoryRow(categoryName)));

		// Törlés
		waitAndClick(deleteButton(categoryName));
	}

	/**
	 * Ellenőrzi, hogy megjelent-e a "Category deleted." toast üzenet.
	 *
	 * @return true, ha az üzenet 10 mp-en belül megjelent; különben false
	 */
	public boolean isDeleteSuccessful() {
	    try {
	        return wait.until(ExpectedConditions.textToBePresentInElementLocated(successMessage,"Category deleted."));

	    } catch (org.openqa.selenium.TimeoutException e) {
	        return false;
	    }
	}

	/**
	 * Ellenőrzi, hogy a kategória sora eltűnt-e a listából (törlés után az oldal
	 * újratölti a listát).
	 *
	 * <p>Ismert hiba az alkalmazásban: az újratöltött lista kb. 2 percig a böngésző
	 * gyorsítótárából jön, ezért a törölt sor még látszik, és ez a metódus false-t ad.
	 * Lásd: DeleteDataTest és a README "Ismert hiba" része.
	 *
	 * @param categoryName a törölt kategória neve vagy slugja
	 * @return true, ha a sor 10 mp-en belül eltűnt; különben false
	 */
	public boolean isDeleted(String categoryName) {
	    try {
	        return wait.until(ExpectedConditions.invisibilityOfElementLocated(categoryRow(categoryName)));

	    } catch (org.openqa.selenium.TimeoutException e) {
	        return false;
	    }
	}
}
