package tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import pages.PagingProductsPage;

/**
 * A terméklista lapozásának tesztje a Practice Software Testing kezdőoldalán.
 * 
 * Mit tesztel?: a lapozóval végig lehet-e menni az 1–5. oldalon,
 * 				 minden kattintás után az adott oldal lesz-e aktív,
 * 				 és vissza lehet-e lépni az 1. oldalra.
 * 
 * Miért jött létre: a lapozás a termékböngészés alapfunkciója, 
 * 					 bejelentkezés nélkül is elérhető.
 * 
 * A böngésző indítását és bezárását a {@link BaseTest} végzi.
 */
@Order(4)
@DisplayName("Terméklista lapozása")
class PagingProductsTest extends BaseTest {

	/**
	 * Végiglapoz a 2–5. oldalon, mindegyiknél ellenőrzi, hogy aktív lett,
	 * majd visszalép az 1. oldalra.
	 */
	@Test
	@DisplayName("Lapozás az 1–5. oldal között, majd vissza az elsőre")
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
