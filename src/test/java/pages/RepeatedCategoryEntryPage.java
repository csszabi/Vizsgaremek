package pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page Object több kategória egymás utáni felvételéhez (adatvezérelt teszthez)
 * a Practice Software Testing admin felületén.
 * 
 * Folyamat: egyszer bejelentkezik, majd minden kategóriához közvetlen URL-lel
 * megnyitja az Add Category űrlapot, kitölti, elmenti, és megvárja a
 * "Category saved!" üzenetet.
 * 
 * A kattintások, gépelések és választások a {@link BasePage} segédmetódusain
 * keresztül történnek, így bemutató módban (-Dslowmo=...) lassítva futnak.
 */
public class RepeatedCategoryEntryPage extends BasePage {

	// Bejelentkezés
    private By signInButton = By.cssSelector("[data-test='nav-sign-in']");
    private By emailField = By.id("email");
    private By passwordField = By.id("password");
    private By loginButton = By.cssSelector("[data-test='login-submit']");

    // Navigáció az admin menüben
    private By menuButton = By.cssSelector("[data-test='nav-menu']");
    private By categoriesButton = By.cssSelector("[data-test='nav-admin-categories']");
    private By addCategoryButton = By.cssSelector("[data-test='category-add']");

    // Add Category űrlap
    private By parentIdField = By.id("parent_id");
    private By nameField = By.id("name");
    private By slugField = By.id("slug");
    private By saveButton =  By.cssSelector("[data-test='category-submit']");

    // Sikeres mentés után kb. 3 másodpercig látszik
    private By successMessage = By.cssSelector("div.alert.alert-success");

    /** Explicit várakozás (max. 10 mp) az ellenőrzésekhez és az oldalállapotokhoz. */
    private WebDriverWait wait;

    /** A megadott nevű szülőkategória opciója a legördülő listában. */
    private By parentOption(String category) {
    	return By.xpath("//select[@id='parent_id']/option[normalize-space()='" + category + "']");
    }

    /**
     * @param driver a teszt által elindított WebDriver
     */
    public RepeatedCategoryEntryPage(WebDriver driver) {
        super(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /**
     * Közvetlen URL-lel megnyitja az Add Category űrlapot (bejelentkezett admin kell hozzá).
     */
    public void openAddCategoryForm() {
        driver.get("https://practicesoftwaretesting.com/admin/categories/add");

        wait.until(
            ExpectedConditions.visibilityOfElementLocated(nameField)
        );
    }

    /**
     * Bejelentkezik adminként, és megnyitja a kategórialistát.
     *
     * @param email admin e-mail címe
     * @param password admin jelszava
     */
    public void loginAndOpenCategories( String email, String password) {

        driver.get("https://practicesoftwaretesting.com/");

        waitAndClick(signInButton);

        type(emailField, email);
        type(passwordField, password);
        click(loginButton);

        // Megvárjuk a bejelentkezés utáni átirányítást (teljes oldal-újratöltés)
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/auth/login")));

        waitAndClick(menuButton);
        waitAndClick(categoriesButton);

        // Csak megvárjuk, hogy a lista oldal betöltsön (az Add gombra nem kattintunk)
        wait.until(ExpectedConditions.elementToBeClickable(addCategoryButton));

    }

    /**
     * Kitölti és elmenti a már megnyitott Add Category űrlapot.
     *
     * @param name az új kategória neve
     * @param category a szülőkategória neve, ahogy a legördülő listában szerepel
     * @param slug az új kategória slugja; egyedinek kell lennie, és csak kisbetűt,
     *             számot és kötőjelet tartalmazhat
     */
    public void addCategory( String name, String category, String slug) {

        wait.until(ExpectedConditions.visibilityOfElementLocated(nameField));

        // A szülőkategóriák listája aszinkron töltődik be, meg kell várni az opciót
        wait.until(ExpectedConditions.presenceOfElementLocated(parentOption(category)));
        selectByText(parentIdField, category);

        type(nameField, name);
        type(slugField, slug);

        click(saveButton);
    }

    /**
     * Ellenőrzi, hogy megjelent-e a "Category saved!" sikerüzenet. Közvetlenül a
     * mentés után kell hívni, és meg kell várni, mielőtt elnavigálunk az oldalról,
     * különben a mentés félbeszakadhat.
     *
     * @return true, ha az üzenet 10 mp-en belül megjelent; különben false
     */
    public boolean isCategorySavedDisplayed() {
    	try {
    		return wait.until(ExpectedConditions.textToBePresentInElementLocated(successMessage, "Category saved!"));

    	} catch (org.openqa.selenium.TimeoutException e) {
    		return false;
    	}
    }
}
