package pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page Object a kategórialista megtekintéséhez a Practice Software Testing
 * admin felületén.
 *
 * <p><b>Mit modellez:</b> az admin Categories listát. Bejelentkezés adminként →
 * menü → Categories → a lista (Id, Parent_id, Name, Slug oszlopok) betöltése.
 *
 * <p><b>Funkciói:</b>
 * <ul>
 *   <li>{@link #openCategoryList(String, String)} – bejelentkezés és a lista megnyitása</li>
 *   <li>{@link #isCategoryListDisplayed()} – ellenőrzi, hogy a lista nem üres</li>
 *   <li>{@link #containsCategory(String)} – ellenőrzi, hogy egy kategória szerepel-e a listában</li>
 * </ul>
 *
 * <p>A kattintások és gépelések a {@link BasePage} segédmetódusain keresztül
 * történnek, így bemutató módban (-Dslowmo=...) lassítva futnak.
 */
public class CategoryListPage extends BasePage {

	// Bejelentkezés
    private By signInButton =By.cssSelector("[data-test='nav-sign-in']");
    private By emailField = By.id("email");
    private By passwordField = By.id("password");
    private By loginButton = By.cssSelector("[data-test='login-submit']");

    // Navigáció az admin menüben
    private By menuButton = By.cssSelector("[data-test='nav-menu']");
    private By categoriesButton = By.cssSelector("[data-test='nav-admin-categories']");

    // A táblázat adatsorai (a fejléc sora nélkül)
    private By categoryRows = By.cssSelector("table tbody tr");

    /** Explicit várakozás (max. 10 mp) az oldalállapotokhoz. */
    private WebDriverWait wait;

    /**
     * @param driver a teszt által elindított WebDriver
     */
    public CategoryListPage(WebDriver driver) {
        super(driver);

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /**
     * Megnyitja a kezdőoldalt, bejelentkezik, és megnyitja a kategórialistát.
     * Megvárja, hogy legalább egy kategória megjelenjen a listában.
     *
     * @param email admin e-mail címe
     * @param password admin jelszava
     */
    public void openCategoryList(String email, String password) {
        driver.get("https://practicesoftwaretesting.com/");

        waitAndClick(signInButton);

        type(emailField, email);
        type(passwordField, password);
        click(loginButton);

        // Megvárjuk a bejelentkezés utáni átirányítást (teljes oldal-újratöltés)
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/auth/login")));

        waitAndClick(menuButton);
        waitAndClick(categoriesButton);

        // A lista aszinkron töltődik be
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(categoryRows, 0));
    }

    /**
     * @return true, ha a listában van legalább egy kategória
     */
    public boolean isCategoryListDisplayed() {
        return !driver.findElements(categoryRows).isEmpty();
    }

    /**
     * Ellenőrzi, hogy a listában van-e olyan sor, amelynek valamelyik cellája
     * (pl. Name vagy Slug) pontosan a megadott szöveg.
     *
     * @param categoryName a keresett kategória neve vagy slugja
     * @return true, ha a kategória szerepel a listában
     */
    public boolean containsCategory(String categoryName) {
    	// //table//tr[...]                : a táblázat az a sora,
        // .//td[normalize-space()='...']  : amelyben van egy cella, aminek a szövege
        //                                   (szélső szóközök nélkül) pontosan categoryName   
        By categoryNameLocator = By.xpath(
            "//table//tr[.//td[normalize-space()='" + categoryName + "']]"
        );

        return !driver.findElements(categoryNameLocator).isEmpty();
    }
}
