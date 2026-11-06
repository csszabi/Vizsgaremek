package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import pages.AccountPage;
import pages.DashboardPage;
import pages.LoginPage;
import utils.TestConfig;

/**
 * A bejelentkezés tesztjei a Practice Software Testing oldalon.
 *
 * <p><b>Mit tesztel?:</b> helyes adatokkal be lehet-e jelentkezni adminként és
 * vásárlóként, és a bejelentkezés után a megfelelő oldal (admin Dashboard, illetve
 * My account) töltődik-e be; illetve hogy hibás adatokkal a bejelentkezés
 * hibaüzenettel elutasításra kerül-e.
 *
 * <p><b>Miért jött létre?:</b> a bejelentkezés minden admin és vásárlói funkció
 * előfeltétele, ezért külön teszt ellenőrzi, hogy önmagában működik.
 *
 * <p><b>Előfeltételek:</b>
 * <ul>
 *   <li>a tesztelt oldal elérhető, a felhasználó nincs bejelentkezve</li>
 *   <li>létezik a demóoldal beépített admin fiókja (admin@practicesoftwaretesting.com)</li>
 *   <li>létezik a demóoldal beépített vásárlói fiókja (customer@practicesoftwaretesting.com)</li>
 * </ul>
 *
 * <p>A böngésző indítását és bezárását a {@link BaseTest} végzi.
 */
@Order(2)
@DisplayName("Bejelentkezés")
class LoginTest extends BaseTest {

	/**
	 * <b>TC-02.1 – Admin bejelentkezés</b>
	 *
	 * <p><b>Tesztfeltétel:</b> helyes admin e-mail címmel és jelszóval be lehet
	 * jelentkezni, és az oldal az admin Dashboardra irányít.
	 *
	 * <p><b>Előfeltételek:</b>
	 * <ul>
	 *   <li>a kezdőoldal betöltött, a felhasználó nincs bejelentkezve</li>
	 *   <li>létezik az admin fiók (admin@practicesoftwaretesting.com / welcome01)</li>
	 * </ul>
	 *
	 * <p><b>Tesztlépések:</b>
	 * <ol>
	 *   <li>Kezdőoldal → Sign in</li>
	 *   <li>E-mail cím és jelszó megadása</li>
	 *   <li>Login gomb</li>
	 * </ol>
	 *
	 * <p><b>Elvárt eredmény:</b> 10 másodpercen belül betölt az admin Dashboard,
	 * amelynek címe "Sales over the years".
	 */
	@Test
	@DisplayName("TC-02.1: Admin bejelentkezés után betölt a Dashboard")
	void logonSuccessTest () {

		LoginPage loginPage = new LoginPage(driver);
		loginPage.open();

		loginPage.login(TestConfig.ADMIN_EMAIL, TestConfig.ADMIN_PASSWORD);

		DashboardPage dashboardPage = new DashboardPage(driver);
		assertTrue(dashboardPage.isLoaded(), "Bejelentkezés után nem töltött be a Dashboard!");

	}
	
	/**
	 * <b>TC-02.2 – Vásárlói bejelentkezés</b>
	 *
	 * <p><b>Tesztfeltétel:</b> helyes vásárlói e-mail címmel és jelszóval be lehet
	 * jelentkezni, és az oldal a vásárlói fiók oldalára irányít.
	 *
	 * <p><b>Előfeltételek:</b>
	 * <ul>
	 *   <li>a kezdőoldal betöltött, a felhasználó nincs bejelentkezve</li>
	 *   <li>létezik a vásárlói fiók (customer@practicesoftwaretesting.com / welcome01)</li>
	 * </ul>
	 *
	 * <p><b>Tesztlépések:</b>
	 * <ol>
	 *   <li>Kezdőoldal → Sign in</li>
	 *   <li>E-mail cím és jelszó megadása</li>
	 *   <li>Login gomb</li>
	 * </ol>
	 *
	 * <p><b>Elvárt eredmény:</b> 10 másodpercen belül betölt a vásárlói fiók oldala
	 * (/account), amelynek címe "My account".
	 */
	@Test
	@DisplayName("TC-02.2: Vásárlói bejelentkezés után betölt a My account oldal")
	void customerLogonSuccessTest () {

		LoginPage loginPage = new LoginPage(driver);
		loginPage.open();

		loginPage.login(TestConfig.CUSTOMER_EMAIL, TestConfig.CUSTOMER_PASSWORD);

		AccountPage accountPage = new AccountPage(driver);
		assertTrue(accountPage.isLoaded(), "Bejelentkezés után nem töltött be a My account oldal!");
	}

	/**
	 * <b>TC-02.3 – Sikertelen bejelentkezés hibás adatokkal</b>
	 *
	 * <p><b>Tesztfeltétel:</b> nem létező fiókkal / hibás jelszóval nem lehet bejelentkezni;
	 * az oldal hibaüzenettel jelzi ezt, és a login oldalon marad.
	 *
	 * <p><b>Előfeltételek:</b>
	 * <ul>
	 *   <li>a kezdőoldal betöltött, a felhasználó nincs bejelentkezve</li>
	 *   <li>a megadott e-mail címmel nem létezik fiók: a teszt minden futáskor egyedi,
	 *       nem létező címet generál</li>
	 * </ul>
	 *
	 * <p>Szándékosan nem létező fiókkal próbálkozik: ha egy valódi fiókba lépne be többször
	 * rossz jelszóval, az oldal zárolná a fiókot ("Account locked, too many failed attempts"),
	 * ami a többi tesztet is elrontaná.
	 *
	 * <p><b>Tesztlépések:</b>
	 * <ol>
	 *   <li>Kezdőoldal → Sign in</li>
	 *   <li>Nem létező e-mail cím ("nincsilyen{időbélyeg}@uiteszt.com") és hibás jelszó megadása</li>
	 *   <li>Login gomb</li>
	 * </ol>
	 *
	 * <p><b>Elvárt eredmény:</b> 10 másodpercen belül megjelenik az "Invalid email or password"
	 * hibaüzenet, és az oldal a login oldalon (/auth/login) marad.
	 */
	@Test
	@DisplayName("TC-02.3: Sikertelen bejelentkezés nem létező fiókkal")
	void invalidLoginTest () {

		// Egyedi, nem létező e-mail cím, hogy a teszt egyetlen valódi fiókot se zároljon
		String email = "nincsilyen" + System.currentTimeMillis() + "@uiteszt.com";

		LoginPage loginPage = new LoginPage(driver);
		loginPage.open();

		loginPage.attemptLogin(email, "RosszJelszo1!");

		String error = loginPage.waitForLoginError();
		assertEquals("Invalid email or password", error,
				"Nem jelent meg a hibás bejelentkezésre utaló hibaüzenet!");

		assertTrue(driver.getCurrentUrl().contains("/auth/login"),
				"Sikertelen bejelentkezés után az oldalnak a login oldalon kellene maradnia!");
	}

}
