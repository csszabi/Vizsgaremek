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
 * Mit tesztel?: egy admin felhasználó át tudja-e írni egy kategória nevét
 * 				 és slugját, és a mentés után megjelenik-e a "Category saved!" sikerüzenet.
 * 
 * Miért jött létre?: a CRUD-tesztsorozat "Update" lépése ({@link AddDataTest}
 * 					  és {@link DeleteDataTest} mellett).
 * 
 * A teszt önálló: előbb létrehozza a saját kategóriáját az {@link AddDataPage}-dzsel,
 * és azt módosítja, így nem kell, hogy előtte az {@link AddDataTest} lefusson.
 * 
 * A böngésző indítását, a kezdőoldal megnyitását és a bezárást a {@link BaseTest} végzi.
 */
@Order(7)
@DisplayName("Kategória módosítása")
class ModifyDataTest extends BaseTest {

	/**
	 * Létrehoz egy "Automata" kategóriát, majd átnevezi "Manuális"-ra, a slug végére
	 * "-mod"-ot tesz, és ellenőrzi a sikerüzenetet.
	 */
	@Test
	@DisplayName("Kategória nevének és slugjának módosítása")
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
