package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import pages.DashboardPage;
import pages.LoginPage;
import pages.LogoffPage;

/**
 * Sikeres kijelentkezés tesztje a Practice Software Testing oldalon.
 *
 * <p><b>Mit tesztel?:</b> egy bejelentkezett admin ki tud-e jelentkezni a felhasználói
 * menüből, és ezután a login oldalra kerül-e.
 *
 * <p><b>Miért jött létre?:</b> a bejelentkezés ({@link LogonSuccessTest}) párja; azt
 * ellenőrzi, hogy a munkamenet valóban véget ér.
 *
 * <p><b>Előfeltételek:</b>
 * <ul>
 *   <li>a tesztelt oldal elérhető, a felhasználó nincs bejelentkezve</li>
 *   <li>létezik a demóoldal beépített admin fiókja (admin@practicesoftwaretesting.com)</li>
 * </ul>
 *
 * <p>A böngésző indítását és bezárását a {@link BaseTest} végzi.
 */
@Order(3)
@DisplayName("Kijelentkezés")
class LogOffSuccessTest extends BaseTest {

	/**
	 * <b>TC-03 – Kijelentkezés</b>
	 *
	 * <p><b>Tesztfeltétel:</b> bejelentkezett felhasználó a felhasználói menüből ki tud
	 * jelentkezni, és ezzel a munkamenete véget ér.
	 *
	 * <p><b>Előfeltételek:</b>
	 * <ul>
	 *   <li>a kezdőoldal betöltött, a felhasználó nincs bejelentkezve</li>
	 *   <li>létezik az admin fiók (admin@practicesoftwaretesting.com / welcome01)</li>
	 * </ul>
	 *
	 * <p><b>Tesztlépések:</b>
	 * <ol>
	 *   <li>Bejelentkezés adminként (Sign in → e-mail, jelszó → Login)</li>
	 *   <li>A Dashboard betöltésének megvárása</li>
	 *   <li>Jobb felső felhasználói menü → Sign out</li>
	 * </ol>
	 *
	 * <p><b>Elvárt eredmény:</b> 10 másodpercen belül a login oldal (/auth/login) nyílik meg.
	 */
	@Test
	@DisplayName("TC-03: Kijelentkezés után a login oldal jelenik meg")
	void logOffSuccessTest() {

		// 1. Bejelentkezés
		LoginPage loginPage = new LoginPage(driver);
		loginPage.open();
		loginPage.login("admin@practicesoftwaretesting.com","welcome01");

		// 2. Megvárjuk, hogy a Dashboard teljesen betöltsön, különben a menüre
		//    kattintás még a régi oldalon történhet
		assertTrue(new DashboardPage(driver).isLoaded(), "Bejelentkezés után nem töltött be a Dashboard!");

		// 3. Kijelentkezés
		LogoffPage logoffPage = new LogoffPage(driver);
		logoffPage.logoff();

		// 4. Várakozás az átirányításra és ellenőrzés
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		boolean loginPageOpened = wait.until(ExpectedConditions.urlContains("/auth/login"));

		assertTrue(loginPageOpened, "Kijelentkezés után nem a login oldal nyílt meg!");
	}

}
