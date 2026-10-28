package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import pages.AddDataPage;

/**
 * Új adat (kategória) felvételének tesztje a Practice Software Testing admin felületén.
 * 
 * Mit tesztel?: egy admin felhasználó létre tud-e hozni új kategóriát,
 * 				 és a mentés után megjelenik-e a "Category saved!" sikerüzenet.
 * 
 * Miért jött létre?: a CRUD-tesztsorozat "Create" lépése. A módosítás
 * 					  ({@link ModifyDataTest}) és a törlés ({@link DeleteDataTest})
 * 					  tesztje egészíti ki.
 * 
 * A teszt önálló, nem függ más tesztektől. Minden futás egyedi slugot használ,
 * mert az oldal nem enged két azonos slugú kategóriát.
 *
 * A böngésző indítását, a kezdőoldal megnyitását és a bezárást a {@link BaseTest} végzi.
 */
@Order(6)
@DisplayName("Kategória felvétele")
class AddDataTest extends BaseTest {

	/**
	 * Bejelentkezik adminként, létrehoz egy "Automata" nevű kategóriát a
	 * "Hand Tools" alá, majd ellenőrzi a sikerüzenetet.
	 */
	@Test
	@DisplayName("Új kategória felvétele, megjelenik a Category saved! üzenet")
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
