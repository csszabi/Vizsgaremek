package pages;

import org.openqa.selenium.WebDriver;

/**
 * Page Object a bejelentkezéshez a Practice Software Testing oldalon.
 *
 * <p><b>Mit modellez:</b> a bejelentkezési folyamatot. Kezdőoldal → Sign in →
 * e-mail és jelszó → Login. Sikeres bejelentkezés után az oldal teljes újratöltéssel
 * átirányít: admin felhasználót a /admin/dashboard, vásárlót az /account oldalra.
 *
 * <p><b>Funkciói:</b>
 * <ul>
 *   <li>{@link #open()} – a kezdőoldal megnyitása (a {@link BasePage}-ből)</li>
 *   <li>{@link #login(String, String)} – bejelentkezés és az átirányítás megvárása</li>
 * </ul>
 *
 * <p>A bejelentkezés lépéseit a {@link BasePage} közös {@code signIn} metódusa végzi,
 * amelyet a többi Page osztály is használ.
 *
 * <p>A kattintások, gépelések és választások a {@link BasePage} segédmetódusain
 * keresztül történnek, így bemutató módban (-Dslowmo=...) lassítva futnak.
 */
public class LoginPage extends BasePage {

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

}
