package tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import pages.CategoryListPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A kategórialista megjelenítésének tesztje a Practice Software Testing admin felületén.
 * 
 * Mit tesztel?: bejelentkezett admin számára megjelenik-e a kategórialista, és
 * 				 szerepel-e benne egy ismert kategória.
 *
 * Miért jött létre?: a kategóriákkal kapcsolatos tesztek (felvétel, módosítás,törlés)
 * 					  mind erre a listára épülnek, ezért külön ellenőrizzük, hogy betölt.
 *  
 * Az ellenőrzött "Hand Tools" kategória a demóoldal alapadatainak része, ezért
 * a teszt nem függ attól, hogy előtte más teszt létrehozott-e kategóriát.
 *  
 * A böngésző indítását és bezárását a {@link BaseTest} végzi.
 */
@Order(10)
@DisplayName("Kategórialista")
public class CategoryListTest extends BaseTest {

    /**
     * Adminként megnyitja a kategórialistát, és ellenőrzi, hogy nem üres,
     * valamint hogy szerepel benne a "Hand Tools" kategória.
     */
    @Test
    @DisplayName("A kategórialista megjelenik, benne a Hand Tools kategóriával")
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
