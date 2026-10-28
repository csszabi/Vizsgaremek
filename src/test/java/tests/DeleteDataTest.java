package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import pages.AddDataPage;
import pages.DeleteDataPage;

/**
 * Adat (kategória) törlésének tesztje a Practice Software Testing admin felületén.
 * 
 * Mit tesztel?: egy admin felhasználó ki tud-e törölni egy kategóriát,
 * 				 megjelenik-e a "Category deleted." üzenet, és eltűnik-e
 * 				 a kategória a listából.
 * 
 * Miért jött létre?: a CRUD-tesztsorozat "Delete" lépése a ({@link AddDataTest}
 * 					  és {@link ModifyDataTest} mellett).
 * 
 * A teszt önálló: előbb létrehozza a saját kategóriáját az {@link AddDataPage}-dzsel,
 * és azt törli. Így nem függ a futási sorrendtől, és nem töröl más által
 * létrehozott vagy termékhez kötött kategóriát.
 * 
 * A böngésző indítását, a kezdőoldal megnyitását és a bezárást a {@link BaseTest} végzi.
 * 
 * ISMERT HIBA (az alkalmazásban, nem a tesztben): a teszt jelenleg elbukik
 * a második ellenőrzésnél ("A kategória sora nem tűnt el a listából!").
 * A törlés sikeres (a "Category deleted." üzenet megjelenik), de törlés után az
 * oldal ugyanarról a címről tölti újra a listát. Emiatt a böngésző kb. 2 percig 
 * a gyorsítótárból adja vissza a régi listát, amelyben a törölt kategória
 * még szerepel. Felhasználói szemmel ez valódi hiba, ezért az ellenőrzést szándékosan
 * nem kerültem meg.
 */
@Order(8)
@DisplayName("Kategória törlése")
class DeleteDataTest extends BaseTest {

	/**
	 * Létrehoz egy "Automata" kategóriát, kitörli, majd ellenőrzi a toast üzenetet
	 * és azt, hogy a sor eltűnt a listából.
	 */
	@Test
	@DisplayName("Kategória törlése, eltűnik a listából (ismert hiba miatt jelenleg elbukik)")
	void deleteDataTest () {

		// A teszt maga hozza létre a törlendő kategóriát, így nem függ más teszttől
		String slug = "teszt-" + System.currentTimeMillis();

		AddDataPage addDataPage = new AddDataPage(driver);
		addDataPage.addData("admin@practicesoftwaretesting.com", "welcome01", "Automata", slug);
		assertTrue(addDataPage.isCategorySavedDisplayed(), "Az előfeltétel kategória nem jött létre!");

		// Törlés: a sort a slug alapján keresi meg
		DeleteDataPage deleteDataPage = new DeleteDataPage(driver);
		deleteDataPage.deleteCategory(slug);

		assertTrue(deleteDataPage.isDeleteSuccessful(), "A Category deleted. üzenet nem jelent meg!");

		// ISMERT HIBA: ez az ellenőrzés jelenleg elbukik, mert az oldal törlés után
		// a böngésző gyorsítótárából (max. 2 percig) a régi listát mutatja.
		// Részletek az osztály leírásában és a README "Ismert hibák" részében.
		assertTrue(deleteDataPage.isDeleted(slug), "A kategória sora nem tűnt el a listából!");
	}
}
