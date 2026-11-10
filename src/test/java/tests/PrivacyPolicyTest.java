package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import pages.PrivacyPolicyPage;

/**
 * Az adatkezelési nyilatkozat (Privacy Policy) tesztje a Practice Software Testing oldalon.
 *
 * <p><b>Mit tesztel?:</b> a kezdőoldal láblécéből meg lehet-e nyitni az adatkezelési
 * nyilatkozatot, és a megnyíló oldalon a nyilatkozat szövege jelenik-e meg.
 *
 * <p><b>Miért jött létre?:</b> az "Adatkezelési nyilatkozat használata" követelmény lefedésére.
 * Az oldalon nincs elfogadandó nyilatkozat (pl. jelölőnégyzet a regisztrációnál), ezért a
 * teszt a nyilatkozat elérhetőségét és tartalmát ellenőrzi.
 *
 * <p><b>Előfeltételek:</b>
 * <ul>
 *   <li>a tesztelt oldal elérhető (bejelentkezés nem kell)</li>
 * </ul>
 *
 * <p>A böngésző indítását és bezárását a {@link BaseTest} végzi.
 */
@Order(11)
@DisplayName("Adatkezelési nyilatkozat")
class PrivacyPolicyTest extends BaseTest {

	/**
	 * <b>TC-11 – Adatkezelési nyilatkozat megnyitása</b>
	 *
	 * <p><b>Tesztfeltétel:</b> a felhasználó bármelyik oldal láblécéből eléri az adatkezelési
	 * nyilatkozatot, és el tudja olvasni annak szövegét.
	 *
	 * <p><b>Előfeltételek:</b>
	 * <ul>
	 *   <li>a kezdőoldal betöltött, a felhasználó nincs bejelentkezve</li>
	 * </ul>
	 *
	 * <p><b>Tesztlépések:</b>
	 * <ol>
	 *   <li>Kezdőoldal megnyitása</li>
	 *   <li>Kattintás a lábléc Privacy Policy linkjére</li>
	 *   <li>A nyilatkozat oldalának (/privacy) megvárása</li>
	 * </ol>
	 *
	 * <p><b>Elvárt eredmény:</b> 10 másodpercen belül megnyílik a /privacy oldal, a böngésző
	 * címsorában "Privacy Policy" szerepel, a főcím "Privacy Policy for Toolshop", és a szöveg
	 * tartalmazza a hatálybalépés dátumát és az adatok törléséről szóló részt.
	 */
	@Test
	@DisplayName("TC-11: Az adatkezelési nyilatkozat megnyílik a lábléc linkjéről")
	void openPrivacyPolicyTest() {

		// 1–3. Kezdőoldal, majd a lábléc linkje
		PrivacyPolicyPage privacyPolicyPage = new PrivacyPolicyPage(driver);
		privacyPolicyPage.open();
		privacyPolicyPage.openFromFooter();

		// 4. A megnyílt oldal ellenőrzése
		assertTrue(driver.getCurrentUrl().endsWith("/privacy"), "Nem a /privacy oldal nyílt meg!");

		assertTrue(driver.getTitle().contains("Privacy Policy"),
				"A böngésző címsorában nem szerepel a Privacy Policy!");

		assertEquals("Privacy Policy for Toolshop", privacyPolicyPage.getHeading(),
				"Nem az adatkezelési nyilatkozat főcíme jelent meg!");

		String content = privacyPolicyPage.getContent();
		assertTrue(content.contains("Effective Date"), "A nyilatkozatból hiányzik a hatálybalépés dátuma!");
		assertTrue(content.contains("Data Removal"), "A nyilatkozatból hiányzik az adatok törléséről szóló rész!");
	}

}
