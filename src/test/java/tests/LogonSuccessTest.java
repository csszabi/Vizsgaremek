package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import pages.DashboardPage;
import pages.LoginPage;

/**
 * Sikeres bejelentkezés tesztje a Practice Software Testing oldalon.
 *
 * Mit tesztel?: helyes admin adatokkal be lehet-e jelentkezni, és a bejelentkezés
 * 				 után betölt-e az admin Dashboard ("Sales over the years" cím).
 * 
 * Miért jött létre?: a bejelentkezés minden admin funkció előfeltétele, ezért
 *				       külön teszt ellenőrzi, hogy önmagában működik.
 * 
 * A böngésző indítását és bezárását a {@link BaseTest} végzi.
 */
@Order(2)
@DisplayName("Bejelentkezés")
class LogonSuccessTest extends BaseTest {

	/**
	 * Adminként bejelentkezik, és ellenőrzi, hogy a Dashboard betöltött.
	 */
	@Test
	@DisplayName("Admin bejelentkezés után betölt a Dashboard")
	void logonSuccessTest () {

		LoginPage loginPage = new LoginPage(driver);
		loginPage.open();

		loginPage.login("admin@practicesoftwaretesting.com", "welcome01");

		DashboardPage dashboardPage = new DashboardPage(driver);
		assertTrue(dashboardPage.isLoaded(), "Bejelentkezés után nem töltött be a Dashboard!");

	}

}
