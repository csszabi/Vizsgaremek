package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import pages.AddDataPage;
import pages.ModifyDataPage;

/**
 * Meglévő adat (kategória) módosításának tesztje a Practice Software Testing
 * admin felületén.
 *
 * <p><b>Mit tesztel?:</b> egy admin felhasználó át tudja-e írni egy kategória nevét
 * és slugját, és a mentés után megjelenik-e a "Category saved!" sikerüzenet.
 *
 * <p><b>Miért jött létre?:</b> a CRUD-tesztsorozat "Update" lépése. 
 *
 * <p><b>Előfeltételek:</b>
 * <ul>
 *   <li>a tesztelt oldal elérhető, a felhasználó nincs bejelentkezve</li>
 *   <li>létezik a demóoldal beépített admin fiókja (admin@practicesoftwaretesting.com)</li>
 *   <li>létezik a "Hand Tools" szülőkategória (a demóoldal alapadatainak része)</li>
 * </ul>
 *
 * <p>A teszt önálló: előbb létrehozza a saját kategóriáját az {@link AddDataPage}-dzsel,
 * és azt módosítja, így nem kell, hogy előtte az {@link AddDataTest} lefusson.
 *
 * <p>A böngésző indítását, a kezdőoldal megnyitását és a bezárást a {@link BaseTest} végzi.
 */
@Order(7)
@DisplayName("Kategória módosítása")
class ModifyDataTest extends BaseTest {

	/**
	 * <b>TC-07 – Kategória módosítása</b>
	 *
	 * <p><b>Tesztfeltétel:</b> egy meglévő kategória neve és slugja módosítható,
	 * és a mentést az oldal visszaigazolja.
	 *
	 * <p><b>Előfeltételek:</b>
	 * <ul>
	 *   <li>a kezdőoldal betöltött, a felhasználó nincs bejelentkezve</li>
	 *   <li>létezik az admin fiók (admin@practicesoftwaretesting.com / welcome01)</li>
	 *   <li>létezik a módosítandó kategória: a teszt az 1–2. lépésben maga hozza létre</li>
	 * </ul>
	 *
	 * <p><b>Tesztlépések:</b>
	 * <ol>
	 *   <li>Bejelentkezés adminként (Sign in → e-mail, jelszó → Login)</li>
	 *   <li>Előkészítés: "Automata" nevű kategória felvétele egyedi sluggal
	 *       ("teszt-{időbélyeg}") a "Hand Tools" alá, a sikerüzenet ellenőrzése</li>
	 *   <li>Menü → Categories → a kategória sorában Edit</li>
	 *   <li>Az űrlap betöltésének megvárása, majd név: "Manuális", slug: az eredeti slug
	 *       "-mod" utótaggal</li>
	 *   <li>Save gomb</li>
	 * </ol>
	 *
	 * <p><b>Elvárt eredmény:</b> 10 másodpercen belül megjelenik a "Category saved!" sikerüzenet.
	 */
	@Test
	@DisplayName("TC-07: Kategória nevének és slugjának módosítása")
	void modifyDataTest () {

		// A teszt maga hozza létre a módosítandó kategóriát, így nem függ más teszttől
		String slug = "teszt-" + System.currentTimeMillis();

		AddDataPage addDataPage = new AddDataPage(driver);
		addDataPage.addData("admin@practicesoftwaretesting.com", "welcome01", "Automata", slug);
		assertTrue(addDataPage.isCategorySavedDisplayed(), "Az előfeltétel kategória nem jött létre!");

		// Módosítás: a sort a slug alapján keresi meg
		ModifyDataPage modifyDataPage = new ModifyDataPage(driver);
		modifyDataPage.modifyCategory(slug, "Manuális", slug + "-mod");

		assertTrue(
			    modifyDataPage.isModifySuccessful(),
			    "A Category saved! sikerüzenet nem jelent meg módosítás után!"
			);
	}

}
