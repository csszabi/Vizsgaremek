package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import pages.RegistrationPage;

/**
 * Sikeres regisztráció tesztje a Practice Software Testing oldalon.
 *
 * <p><b>Mit tesztel?:</b> érvényes adatokkal létre lehet-e hozni új vásárlói fiókot,
 * és a regisztráció után a login oldalra kerül-e a felhasználó.
 *
 * <p><b>Miért jött létre?:</b> a regisztráció az új felhasználók első lépése, ezért ez
 * a tesztsorozat első tesztje.
 *
 * <p><b>Előfeltételek:</b>
 * <ul>
 *   <li>a tesztelt oldal elérhető, a felhasználó nincs bejelentkezve</li>
 *   <li>a megadott e-mail címmel még nincs regisztrált fiók; ezt a teszt biztosítja,
 *       mert minden futás egyedi e-mail címet generál (már regisztrált címmel
 *       a regisztráció hibát ad: "Email is already in use.")</li>
 * </ul>
 *
 * <p>A böngésző indítását, a kezdőoldal megnyitását és a bezárást a {@link BaseTest} végzi.
 */
@Order(1)
@DisplayName("Regisztráció")
class RegistrationSuccessTest extends BaseTest {

	/**
	 * <b>TC-01 – Sikeres regisztráció új vásárlói fiókkal</b>
	 *
	 * <p><b>Tesztfeltétel:</b> érvényes, a szabályoknak megfelelő adatokkal új vásárlói
	 * fiók hozható létre, és az oldal a regisztráció után a bejelentkezéshez irányít.
	 *
	 * <p><b>Előfeltételek:</b>
	 * <ul>
	 *   <li>a kezdőoldal betöltött, a felhasználó nincs bejelentkezve</li>
	 *   <li>egyedi, még nem regisztrált e-mail cím ("johndozen{időbélyeg}@uiteszt.com")</li>
	 * </ul>
	 *
	 * <p><b>Tesztlépések:</b>
	 * <ol>
	 *   <li>Kezdőoldal → Sign in → Register your account</li>
	 *   <li>Személyes adatok: keresztnév "John", vezetéknév "Dozen",
	 *       születési dátum "1984-09-21"</li>
	 *   <li>Cím: ország "Hungary", irányítószám "1149", házszám "15"; az automatikus
	 *       cím-lekérdezés után utca "Teszt Street", város "Budapest", megye "Pest"</li>
	 *   <li>Telefonszám "061123467", az egyedi e-mail cím, jelszó "JohnDozen1!"</li>
	 *   <li>Register gomb</li>
	 * </ol>
	 *
	 * <p><b>Elvárt eredmény:</b> az oldal 10 másodpercen belül a login oldalra
	 * (/auth/login) irányít. Ha nem, a teszt kiírja az oldal hibaüzenetét is.
	 */
	@Test
	@DisplayName("TC-01: Sikeres regisztráció új, egyedi e-mail címmel")
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
