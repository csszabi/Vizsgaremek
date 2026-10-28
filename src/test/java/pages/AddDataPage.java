package pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page Object az új kategória felvételéhez a Practice Software Testing oldal
 * admin felületén.
 * 
 * Lefedett folyamat: bejelentkezés adminként → menü → Categories → Add Category →
 * űrlap kitöltése (szülőkategória, név, slug) → Save → "Category saved!" üzenet.
 * 
 * A {@link #login}, {@link #openCategories} és {@link #createCategory} lépések
 * külön is hívhatók, így más tesztek (pl. módosítás, törlés) ezzel az osztállyal
 * készíthetik elő a saját tesztadatukat.
 * 
 * A kattintások, gépelések és választások a {@link BasePage} segédmetódusain
 * keresztül történnek, így bemutató módban (-Dslowmo=...) lassítva futnak.
 */
public class AddDataPage extends BasePage {

	// Bejelentkezés
	private By signInButton = By.cssSelector("[data-test='nav-sign-in']");
	private By emailAddressField = By.id("email");
	private By passwordField = By.id("password");
	private By loginButton = By.cssSelector("[data-test='login-submit']");

	// Navigáció az admin menüben
	private By menuButton = By.cssSelector("[data-test='nav-menu']");
	private By categoriesButton = By.cssSelector("[data-test='nav-admin-categories']");

	// Add Category űrlap
	private By addCategoryButton = By.cssSelector("[data-test='category-add']");
	private By parentIdField = By.id("parent_id");
	private By handToolsOption = By.xpath("//select[@id='parent_id']/option[normalize-space()='Hand Tools']");
	private By nameField = By.id("name");
	private By slugField = By.id("slug");
	private By saveButton = By.cssSelector("[data-test='category-submit']");

	// Sikeres mentés után kb. 3 másodpercig látszik, utána eltűnik
	private By categorySavedMessage = By.cssSelector("div[role='alert'].alert-success");

	/** Explicit várakozás (max. 10 mp) az ellenőrzésekhez és az átirányításhoz. */
	private WebDriverWait wait;

	/**
	 * @param driver a teszt által elindított WebDriver
	 */
	public AddDataPage(WebDriver driver) {
		super(driver);
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	}

	/**
	 * A teljes folyamat egyben: bejelentkezés, kategóriák oldal, új kategória mentése.
	 * A kezdőoldalról, kijelentkezett állapotból kell indítani.
	 *
	 * @param email admin e-mail címe
	 * @param password admin jelszava
	 * @param name az új kategória neve
	 * @param slug az új kategória slugja; egyedinek kell lennie, és csak kisbetűt,
	 *             számot és kötőjelet tartalmazhat
	 */
	public void addData (String email, String password, String name, String slug) {

		login(email, password);
		openCategories();
		createCategory(name, slug);
	}

	/**
	 * Bejelentkezik a megadott adatokkal (a kezdőoldal Sign in linkjéről indul).
	 *
	 * @param email felhasználó e-mail címe
	 * @param password felhasználó jelszava
	 */
	public void login(String email, String password) {

		waitAndClick(signInButton);

		type(emailAddressField, email);
		type(passwordField, password);
		click(loginButton);

		// Az átirányítás teljes oldal-újratöltés: amíg le nem zajlik, a menü
		// a régi oldalon is látszhat, és a rákattintás elveszne
		wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/auth/login")));
	}

	/**
	 * Megnyitja az admin menüből a Categories listát. Bejelentkezett admin kell hozzá.
	 */
	public void openCategories() {

		waitAndClick(menuButton);
		waitAndClick(categoriesButton);
	}

	/**
	 * A Categories listáról megnyitja az Add Category űrlapot, kitölti és elmenti.
	 * Szülőkategóriának mindig a "Hand Tools"-t választja.
	 *
	 * @param name az új kategória neve
	 * @param slug az új kategória egyedi slugja
	 */
	public void createCategory(String name, String slug) {

		waitAndClick(addCategoryButton);

		// A szülő kategóriák listája aszinkron töltődik be, meg kell várni az opciót
		wait.until(ExpectedConditions.presenceOfElementLocated(handToolsOption));
		selectByText(parentIdField, "Hand Tools");

		type(nameField, name);
		type(slugField, slug);

		click(saveButton);
	}

	/**
	 * Ellenőrzi, hogy megjelent-e a "Category saved!" sikerüzenet.
	 * Közvetlenül a mentés után kell hívni, mert az üzenet pár másodperc után eltűnik.
	 *
	 * @return true, ha az üzenet 10 mp-en belül megjelent; különben false
	 */
	public boolean isCategorySavedDisplayed() {
		try {
			return wait.until(
				ExpectedConditions.visibilityOfElementLocated(categorySavedMessage)
			).getText().contains("Category saved!");

		} catch (org.openqa.selenium.TimeoutException e) {
			return false;
		}
	}

}
