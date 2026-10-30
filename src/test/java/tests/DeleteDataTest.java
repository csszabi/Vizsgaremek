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
 * <p><b>Mit tesztel?:</b> egy admin felhasználó ki tud-e törölni egy kategóriát,
 * megjelenik-e a "Category deleted." üzenet, és eltűnik-e a kategória a listából.
 *
 * <p><b>Miért jött létre?:</b> a CRUD-tesztsorozat "Delete" lépése.
 *
 * <p><b>Előfeltételek:</b>
 * <ul>
 *   <li>a tesztelt oldal elérhető, a felhasználó nincs bejelentkezve</li>
 *   <li>létezik a demóoldal beépített admin fiókja (admin@practicesoftwaretesting.com)</li>
 *   <li>létezik a "Hand Tools" szülőkategória (a demóoldal alapadatainak része)</li>
 * </ul>
 *
 * <p>A teszt önálló: előbb létrehozza a saját kategóriáját az {@link AddDataPage}-dzsel,
 * és azt törli. Így nem függ a futási sorrendtől, és nem töröl más által
 * létrehozott vagy termékhez kötött kategóriát.
 *
 * <p>A böngésző indítását, a kezdőoldal megnyitását és a bezárást a {@link BaseTest} végzi.
 *
 * <p><b>ISMERT HIBA</b> (az alkalmazásban, nem a tesztben): a teszt jelenleg elbukik
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
	 * <b>TC-08 – Kategória törlése</b>
	 *
	 * <p><b>Tesztfeltétel:</b> egy termékhez nem kötött kategória törölhető, a törlést az
	 * oldal visszaigazolja, és a kategória eltűnik a listából.
	 *
	 * <p><b>Előfeltételek:</b>
	 * <ul>
	 *   <li>a kezdőoldal betöltött, a felhasználó nincs bejelentkezve</li>
	 *   <li>létezik az admin fiók (admin@practicesoftwaretesting.com / welcome01)</li>
	 *   <li>létezik a törlendő, termékhez nem kötött kategória: a teszt az 1–2. lépésben
	 *       maga hozza létre</li>
	 * </ul>
	 *
	 * <p><b>Tesztlépések:</b>
	 * <ol>
	 *   <li>Bejelentkezés adminként (Sign in → e-mail, jelszó → Login)</li>
	 *   <li>Előkészítés: "Automata" nevű kategória felvétele egyedi sluggal
	 *       ("teszt-{időbélyeg}") a "Hand Tools" alá, a sikerüzenet ellenőrzése</li>
	 *   <li>Menü → Categories → a kategória sorában Delete</li>
	 * </ol>
	 *
	 * <p><b>Elvárt eredmény:</b>
	 * <ul>
	 *   <li>10 másodpercen belül megjelenik a "Category deleted." üzenet</li>
	 *   <li>10 másodpercen belül eltűnik a kategória sora a listából</li>
	 * </ul>
	 *
	 * <p>Ismert hiba miatt (lásd az osztály leírását) a második elvárt eredmény jelenleg
	 * nem teljesül, ezért a teszt elbukik.
	 */
	@Test
	@DisplayName("TC-08: Kategória törlése, eltűnik a listából (ismert hiba miatt jelenleg elbukik)")
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
		// Részletek az osztály leírásában és a README "Ismert hiba" részében.
		assertTrue(deleteDataPage.isDeleted(slug), "A kategória sora nem tűnt el a listából!");
	}
}
