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
 * Sikertelen bejelentkezésnél a login oldalon marad, és hibaüzenetet ad.
 *
 * <p><b>Funkciói:</b>
 * <ul>
 *   <li>{@link #open()} – a kezdőoldal megnyitása (a {@link BasePage}-ből)</li>
 *   <li>{@link #login(String, String)} – bejelentkezés és az átirányítás megvárása</li>
 *   <li>{@link #attemptLogin(String, String)} – bejelentkezési kísérlet az eredmény
 *       megvárása nélkül (sikertelen belépés teszteléséhez)</li>
 *   <li>{@link #waitForLoginError()} – a bejelentkezési hibaüzenet megvárása</li>
 * </ul>
 *
 * <p>A bejelentkezés lépéseit a {@link BasePage} közös {@code signIn} és
 * {@code submitLogin} metódusa végzi, amelyet a többi Page osztály is használ.
 *
 * <p>A kattintások, gépelések és választások a {@link BasePage} segédmetódusain
 * keresztül történnek, így bemutató módban (-Dslowmo=...) lassítva futnak.
 */
public class LoginPage extends BasePage {

	// Sikertelen bejelentkezéskor megjelenő hibaüzenet ("Invalid email or password")
	private By loginError = By.cssSelector("[data-test='login-error']");

	/**
	 * @param driver a teszt által elindított WebDriver
	 */
	public LoginPage(WebDriver driver) {
		super(driver);
	}

	/**
	 * Bejelentkezik, és megvárja, hogy az oldal átirányítson a login oldalról.
	 * A kezdőoldalról, kijelentkezett állapotból kell indítani.
	 *
	 * @param email felhasználó e-mail címe
	 * @param password felhasználó jelszava
	 */
	public void login(String email, String password) {
		signIn(email, password);
	}

	/**
	 * Kitölti és elküldi a bejelentkezési űrlapot, de nem vár átirányításra.
	 * Sikertelen bejelentkezés tesztelésére szolgál; az eredményt a
	 * {@link #waitForLoginError()} metódussal lehet ellenőrizni.
	 *
	 * @param email felhasználó e-mail címe
	 * @param password felhasználó jelszava
	 */
	public void attemptLogin(String email, String password) {
		submitLogin(email, password);
	}

	/**
	 * Megvárja, hogy megjelenjen a bejelentkezési hibaüzenet, és visszaadja a szövegét.
	 *
	 * @return a hibaüzenet szövege, vagy üres szöveg, ha 10 mp-en belül nem jelent meg
	 */
	public String waitForLoginError() {
		try {
			return new WebDriverWait(driver, Duration.ofSeconds(10))
				.until(ExpectedConditions.visibilityOfElementLocated(loginError))
				.getText();
		} catch (org.openqa.selenium.TimeoutException e) {
			return "";
		}
	}

}
