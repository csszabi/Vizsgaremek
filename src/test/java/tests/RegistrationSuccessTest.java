package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import pages.RegistrationPage;

/**
 * Sikeres regisztráció tesztje a Practice Software Testing oldalon.
 * 
 * Mit tesztel?: érvényes adatokkal létre lehet-e hozni új vásárlói fiókot, és
 * 				 a regisztráció után a login oldalra kerül-e a felhasználó.
 * 
 * Miért jött létre?: a regisztráció az új felhasználók első lépése, ezért ez a
 * 					  tesztsorozat első tesztje.
 * 
 * Minden futás egyedi e-mail címet használ, mert már regisztrált címmel
 * a regisztráció hibát ad ("Email is already in use.").
 * 
 * A böngésző indítását és bezárását a {@link BaseTest} végzi.
 */
@Order(1)
@DisplayName("Regisztráció")
class RegistrationSuccessTest extends BaseTest {

	/**
	 * Kitölti a regisztrációs űrlapot egy egyedi e-mail címmel, majd ellenőrzi,
	 * hogy a regisztráció sikeres volt.
	 */
	@Test
	@DisplayName("Sikeres regisztráció új, egyedi e-mail címmel")
	void registrationSuccessTest () {

		// Egyedi e-mail cím minden futáshoz: az aktuális időbélyeg (ezredmásodpercben)
		// miatt nem ütközik egy korábbi futásban már regisztrált címmel
		String email = "johndozen" + System.currentTimeMillis() + "@uiteszt.com";

		RegistrationPage registrationPage = new RegistrationPage(driver);
		registrationPage.open();

		registrationPage.register("John", "Dozen", "1984-09-21", "1149", "15", "Teszt Street", "Budapest", "Pest", "061123467", email, "JohnDozen1!");

		assertTrue(
			registrationPage.isRegistrationSuccessful(),
			"A regisztráció nem sikerült! Hibaüzenet: " + registrationPage.getRegistrationError()
		);
	}

}
