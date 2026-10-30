package tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import pages.CategoryListPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A kategórialista megjelenítésének tesztje a Practice Software Testing admin felületén.
 *
 * <p><b>Mit tesztel?:</b> bejelentkezett admin számára megjelenik-e a kategórialista, és
 * szerepel-e benne egy ismert kategória.
 *
 * <p><b>Miért jött létre?:</b> a kategóriákkal kapcsolatos tesztek (felvétel, módosítás,
 * törlés) mind erre a listára épülnek, ezért külön ellenőrizzük, hogy betölt.
 *
 * <p><b>Előfeltételek:</b>
 * <ul>
 *   <li>a tesztelt oldal elérhető, a felhasználó nincs bejelentkezve</li>
 *   <li>létezik a demóoldal beépített admin fiókja (admin@practicesoftwaretesting.com)</li>
 *   <li>létezik a "Hand Tools" szülőkategória (a demóoldal alapadatainak része)</li>
 * </ul>
 *
 * <p>Mivel a "Hand Tools" kategória a demóoldal alapadatainak része, a teszt nem függ
 * attól, hogy előtte más teszt létrehozott-e kategóriát.
 *
 * <p>A böngésző indítását és bezárását a {@link BaseTest} végzi.
 */
@Order(10)
@DisplayName("Kategórialista")
public class CategoryListTest extends BaseTest {

    /**
     * <b>TC-10 – Kategórialista megjelenítése</b>
     *
     * <p><b>Tesztfeltétel:</b> bejelentkezett admin számára betölt a kategórialista,
     * és tartalmazza a demóoldal alapkategóriáit.
     *
     * <p><b>Előfeltételek:</b>
     * <ul>
     *   <li>a tesztelt oldal elérhető, a felhasználó nincs bejelentkezve</li>
     *   <li>létezik az admin fiók (admin@practicesoftwaretesting.com / welcome01)</li>
     *   <li>létezik a "Hand Tools" kategória</li>
     * </ul>
     *
     * <p><b>Tesztlépések:</b>
     * <ol>
     *   <li>Kezdőoldal → Sign in → bejelentkezés adminként</li>
     *   <li>Menü → Categories</li>
     *   <li>A lista betöltésének megvárása</li>
     * </ol>
     *
     * <p><b>Elvárt eredmény:</b> a lista legalább egy kategóriát tartalmaz, és van benne
     * "Hand Tools" sor.
     */
    @Test
    @DisplayName("TC-10: A kategórialista megjelenik, benne a Hand Tools kategóriával")
    void categoryListTest() {

        CategoryListPage page = new CategoryListPage(driver);

        page.openCategoryList(
            "admin@practicesoftwaretesting.com",
            "welcome01"
        );

        assertTrue(
            page.isCategoryListDisplayed(),
            "A kategórialista üres vagy nem jelent meg!"
        );

        assertTrue(
            page.containsCategory("Hand Tools"),
            "A Hand Tools kategória nem található a kategórialistában!"
        );
    }
}
