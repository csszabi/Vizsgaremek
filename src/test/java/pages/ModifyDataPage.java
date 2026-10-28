package pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page Object egy meglévő kategória módosításához a Practice Software Testing
 * admin felületén.
 * 
 * Lefedett folyamat: menü → Categories → a kategória sorában Edit →
 * név és slug átírása → Save → "Category saved!" üzenet.
 * 
 * Az Edit Category űrlap ugyanazt a felületet használja, mint az Add Category,
 * ezért a mezők lokátorai és a sikerüzenet megegyeznek az {@link AddDataPage}-ével.
 * 
 * A kattintások és gépelések a {@link BasePage} segédmetódusain keresztül
 * történnek, így bemutató módban (-Dslowmo=...) lassítva futnak.
 */
public class ModifyDataPage extends BasePage {

	// Bejelentkezés
	private By signInButton = By.cssSelector("[data-test='nav-sign-in']");
	private By emailAddressField = By.id("email");
	private By passwordField = By.id("password");
	private By loginButton = By.cssSelector("[data-test='login-submit']");

	// Navigáció az admin menüben
	private By menuButton = By.cssSelector("[data-test='nav-menu']");
	private By categoriesButton = By.cssSelector("[data-test='nav-admin-categories']");

	// Edit Category űrlap
	private By nameField = By.id("name");
	private By slugField = By.id("slug");
	private By saveButton = By.cssSelector("[data-test='category-submit']");
	private By categorySavedMessage = By.cssSelector("div[role='alert'].alert-success");

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

	/** Az adott sor Edit linkje (data-test="category-edit-{id}"). */
	private By editButton(String categoryName) {
		// //tr[...]                       : az a táblázatsor,
	    // .//td[normalize-space()='...']  : amelyben van egy cella, aminek a szövege pontosan categoryName,
	    // //a[contains(@data-test, ...)]  : és azon a soron belül az Edit link
	    //                                   (a data-test értéke "category-edit-{id}", ezért elég az elejére illeszteni)
	    return By.xpath("//tr[.//td[normalize-space()='" + categoryName + "']]"+ "//a[contains(@data-test, 'category-edit-')]");

	}

	/** Explicit várakozás (max. 10 mp) az ellenőrzésekhez és az oldalállapotokhoz. */
	private WebDriverWait wait;

	/**
	 * @param driver a teszt által elindított WebDriver
	 */
	public ModifyDataPage(WebDriver driver) {
		super(driver);
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	}

	/**
	 * A teljes folyamat bejelentkezéssel együtt: a kezdőoldalról,
	 * kijelentkezett állapotból kell indítani.
	 *
	 * @param email admin e-mail címe
	 * @param password admin jelszava
	 * @param categoryName a módosítandó kategória neve vagy slugja (ez alapján keresi a sort)
	 * @param name az új név
	 * @param slug az új slug
	 */
	public void modifyData (String email, String password, String categoryName, String name, String slug) {

		waitAndClick(signInButton);

		type(emailAddressField, email);
		type(passwordField, password);
		click(loginButton);

		// Megvárjuk a bejelentkezés utáni átirányítást (teljes oldal-újratöltés)
		wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/auth/login")));

		modifyCategory(categoryName, name, slug);
	}

	/**
	 * Módosít egy kategóriát. Már bejelentkezett admin kell hozzá (bármelyik admin
	 * oldalról indítható, mert a menüből nyitja meg a Categories listát).
	 *
	 * @param categoryName a módosítandó kategória neve vagy slugja (ez alapján keresi a sort)
	 * @param name az új név
	 * @param slug az új slug
	 */
	public void modifyCategory (String categoryName, String name, String slug) {

		waitAndClick(menuButton);
		waitAndClick(categoriesButton);

		// A módosítandó kategória sorának megvárása, majd Edit
		wait.until(ExpectedConditions.visibilityOfElementLocated(categoryRow(categoryName)));
		waitAndClick(editButton(categoryName));

		// Az űrlapot az oldal aszinkron tölti fel a meglévő adatokkal,
		// ha előbb írunk bele, a betöltés felülírja
		WebElement nameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(nameField));
		wait.until(d -> !nameInput.getAttribute("value").isEmpty());

		replaceText(nameInput, name);
		replaceText(find(slugField), slug);

		click(saveButton);
	}

	/**
	 * Lecseréli egy beviteli mező tartalmát.
	 * A clear() nem vált ki input eseményt, így az Angular űrlap a régi értéket
	 * tartaná meg, ezért Ctrl+A + Backspace törlést használunk.
	 * Közvetlenül a sendKeys()-t hívja, ezért itt külön kell a bemutató módú pause().
	 */
	private void replaceText(WebElement input, String text) {
		pause();
		input.click(); 							// fókusz a mezőre
		input.sendKeys(Keys.CONTROL, "a");		// teljes szöveg kijelölés
		input.sendKeys(Keys.BACK_SPACE);		// törlés
		input.sendKeys(text);					// új érték beírása
	}

	/**
	 * Ellenőrzi, hogy mentés után megjelent-e a "Category saved!" üzenet.
	 *
	 * @return true, ha az üzenet 10 mp-en belül megjelent; különben false
	 */
	public boolean isModifySuccessful() {
	    try {
	        return wait.until(ExpectedConditions.textToBePresentInElementLocated(categorySavedMessage,"Category saved!"));

	    } catch (org.openqa.selenium.TimeoutException e) {
	        return false;
	    }
	}

}
