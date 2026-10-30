package pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page Object a bejelentkezéshez a Practice Software Testing oldalon.
 *
 * <p><b>Mit modellez:</b> a bejelentkezési folyamatot. Kezdőoldal → Sign in →
 * e-mail és jelszó → Login. Sikeres bejelentkezés után az oldal teljes újratöltéssel
 * átirányít: admin felhasználót a /admin/dashboard, vásárlót az /account oldalra.
 *
 * <p><b>Funkciói:</b>
 * <ul>
 *   <li>{@link #open()} – a kezdőoldal megnyitása</li>
 *   <li>{@link #login(String, String)} – bejelentkezés és az átirányítás megvárása</li>
 * </ul>
 *
 * <p>A kattintások, gépelések és választások a {@link BasePage} segédmetódusain
 * keresztül történnek, így bemutató módban (-Dslowmo=...) lassítva futnak.
 */
public class LoginPage extends BasePage {

	private By signInButton = By.cssSelector("[data-test='nav-sign-in']");
	private By emailAddressField = By.id("email");
	private By passwordField = By.id("password");
	private By loginButton = By.cssSelector("[data-test='login-submit']");

	/** Explicit várakozás (max. 10 mp) az átirányításhoz. */
	private WebDriverWait wait;

	/**
	 * @param driver a teszt által elindított WebDriver
	 */
	public LoginPage(WebDriver driver) {
		super(driver);
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	}

	/** Megnyitja az oldal kezdőlapját. */
	public void open( ) {

		driver.get("https://practicesoftwaretesting.com/");
	}

	/**
	 * Bejelentkezik, és megvárja, hogy az oldal átirányítson a login oldalról.
	 * A kezdőoldalról, kijelentkezett állapotból kell indítani.
	 *
	 * @param email felhasználó e-mail címe
	 * @param password felhasználó jelszava
	 */
	public void login(String email, String password) {

		// Frissen betöltött oldalon az Angular még renderelhet, ezért várni kell a linkre
		waitAndClick(signInButton);

		type(emailAddressField, email);
		type(passwordField, password);
		click(loginButton);

		// Az átirányítás teljes oldal-újratöltés: amíg le nem zajlik, a menü
		// a régi oldalon is látszhat, és a rákattintás elveszne
		wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/auth/login")));
	}

}
