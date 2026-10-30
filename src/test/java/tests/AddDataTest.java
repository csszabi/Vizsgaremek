package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import pages.AddDataPage;

/**
 * Új adat (kategória) felvételének tesztje a Practice Software Testing admin felületén.
 *
 * <p><b>Mit tesztel?:</b> egy admin felhasználó létre tud-e hozni új kategóriát,
 * és a mentés után megjelenik-e a "Category saved!" sikerüzenet.
 *
 * <p><b>Miért jött létre?:</b> a CRUD-tesztsorozat "Create" lépése. 
 *
 * <p><b>Előfeltételek:</b>
 * <ul>
 *   <li>a tesztelt oldal elérhető, a felhasználó nincs bejelentkezve</li>
 *   <li>létezik a demóoldal beépített admin fiókja (admin@practicesoftwaretesting.com)</li>
 *   <li>létezik a "Hand Tools" szülőkategória (a demóoldal alapadatainak része)</li>
 * </ul>
 *
 * <p>A teszt önálló, nem függ más tesztektől. Minden futás egyedi slugot használ,
 * mert az oldal nem enged két azonos slugú kategóriát.
 *
 * <p>A böngésző indítását, a kezdőoldal megnyitását és a bezárást a {@link BaseTest} végzi.
 */
@Order(6)
@DisplayName("Kategória felvétele")
class AddDataTest extends BaseTest {

	/**
	 * <b>TC-06 – Új kategória felvétele</b>
	 *
	 * <p><b>Tesztfeltétel:</b> admin felhasználó új kategóriát tud létrehozni,
	 * és a mentést az oldal visszaigazolja.
	 *
	 * <p><b>Előfeltételek:</b>
	 * <ul>
	 *   <li>a kezdőoldal betöltött, a felhasználó nincs bejelentkezve</li>
	 *   <li>létezik az admin fiók (admin@practicesoftwaretesting.com / welcome01)</li>
	 *   <li>létezik a "Hand Tools" szülőkategória</li>
	 * </ul>
	 *
	 * <p><b>Tesztlépések:</b>
	 * <ol>
	 *   <li>Bejelentkezés adminként (Sign in → e-mail, jelszó → Login)</li>
	 *   <li>Menü → Categories → Add Category</li>
	 *   <li>Szülőkategória: "Hand Tools", név: "Automata", slug: egyedi ("teszt-{időbélyeg}")</li>
	 *   <li>Save gomb</li>
	 * </ol>
	 *
	 * <p><b>Elvárt eredmény:</b> 10 másodpercen belül megjelenik a "Category saved!" sikerüzenet.
	 */
	@Test
	@DisplayName("TC-06: Új kategória felvétele, megjelenik a Category saved! üzenet")
	void addDataTest () {

		// A slugnak egyedinek kell lennie, ezért minden futás saját slugot kap
		String slug = "teszt-" + System.currentTimeMillis();

		AddDataPage addDataPage = new AddDataPage(driver);
		addDataPage.addData("admin@practicesoftwaretesting.com", "welcome01", "Automata", slug);

		assertTrue(
			    addDataPage.isCategorySavedDisplayed(),
			    "A Category saved! sikerüzenet nem jelent meg!"
			);
	}
}
