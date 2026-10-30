package tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import pages.PagingProductsPage;

/**
 * A terméklista lapozásának tesztje a Practice Software Testing kezdőoldalán.
 *
 * <p><b>Mit tesztel?:</b> a lapozóval végig lehet-e menni az 1–5. oldalon, minden
 * kattintás után az adott oldal lesz-e aktív, és vissza lehet-e lépni az 1. oldalra.
 *
 * <p><b>Miért jött létre?:</b> a lapozás a termékböngészés alapfunkciója,
 * bejelentkezés nélkül is elérhető.
 *
 * <p><b>Előfeltételek:</b>
 * <ul>
 *   <li>a tesztelt oldal elérhető (bejelentkezés nem kell)</li>
 *   <li>a terméklista legalább 5 oldalnyi terméket tartalmaz</li>
 * </ul>
 *
 * <p>A böngésző indítását és bezárását a {@link BaseTest} végzi.
 */
@Order(4)
@DisplayName("Terméklista lapozása")
class PagingProductsTest extends BaseTest {

	/**
	 * <b>TC-04 – Terméklista lapozása</b>
	 *
	 * <p><b>Tesztfeltétel:</b> a kezdőoldali terméklista lapozójával bármelyik oldalra
	 * át lehet lépni, és a lapozó mindig a kiválasztott oldalt jelöli aktívnak.
	 *
	 * <p><b>Előfeltételek:</b>
	 * <ul>
	 *   <li>a kezdőoldal betöltött, a lapozó megjelent</li>
	 *   <li>a terméklista legalább 5 oldalas</li>
	 * </ul>
	 *
	 * <p><b>Tesztlépések:</b>
	 * <ol>
	 *   <li>Kezdőoldal megnyitása, a lapozó megvárása</li>
	 *   <li>Kattintás sorban a 2., 3., 4. és 5. oldalra; minden kattintás után ellenőrzés</li>
	 *   <li>Visszalépés az 1. oldalra</li>
	 * </ol>
	 *
	 * <p><b>Elvárt eredmény:</b> minden kattintás után 10 másodpercen belül a kiválasztott
	 * oldalszám lesz aktív a lapozóban (2–5., végül az 1.).
	 */
	@Test
	@DisplayName("TC-04: Lapozás az 1–5. oldal között, majd vissza az elsőre")
	void navigateBetweenPagesTest() {
		PagingProductsPage pagingProductsPage =  new PagingProductsPage(driver);
		pagingProductsPage.open();

		// Végiglapozunk a 2–5. oldalon (az 1. alapból aktív, ezért onnan indulunk),
		// és minden lapozás után ellenőrizzük, hogy a lapozóban tényleg az az oldal lett-e az aktív
		for (int page = 2; page <= 5; page++) {
			pagingProductsPage.goToPage(page);
			assertTrue(pagingProductsPage.isPageActive(page), "A(z) " + page + ". oldalnak aktívnak kell lennie!");
		}

		pagingProductsPage.goToPage(1);

		assertTrue(pagingProductsPage.isPageActive(1), "Az 1-es oldalnak aktívnak kell lennie!");
	}

}
