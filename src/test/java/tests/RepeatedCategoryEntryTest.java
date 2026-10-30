package tests;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import pages.RepeatedCategoryEntryPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Ismételt (adatvezérelt) kategória-felvitel tesztje a Practice Software Testing
 * admin felületén.
 *
 * <p><b>Mit tesztel?:</b> egy CSV fájlban megadott több kategóriát egymás után fel
 * lehet-e venni, és mindegyik mentése után megjelenik-e a "Category saved!" üzenet.
 *
 * <p><b>Miért jött létre?:</b> az ismétlődő adatbevitel automatizálására és annak
 * ellenőrzésére, hogy a felület több egymás utáni mentést is helyesen kezel.
 *
 * <p><b>Előfeltételek:</b>
 * <ul>
 *   <li>a tesztelt oldal elérhető, a felhasználó nincs bejelentkezve</li>
 *   <li>létezik a demóoldal beépített admin fiókja (admin@practicesoftwaretesting.com)</li>
 *   <li>a src/test/resources/categories.csv fájl létezik: pontosvesszővel elválasztva,
 *       fejléc sorral (név;szülőkategória;slug)</li>
 *   <li>a CSV-ben megadott szülőkategóriák léteznek az oldalon</li>
 * </ul>
 *
 * <p>A slugoknak egyedinek kell lenniük, ezért a teszt minden CSV-beli slug végére
 * egy futásonként egyedi utótagot tesz; így a teszt többször is futtatható.
 *
 * <p>A böngésző indítását és bezárását a {@link BaseTest} végzi.
 */
@Order(9)
@DisplayName("Kategóriák felvétele CSV-ből")
public class RepeatedCategoryEntryTest extends BaseTest {

    /**
     * <b>TC-09 – Több kategória felvétele CSV fájlból</b>
     *
     * <p><b>Tesztfeltétel:</b> egy adatfájlban megadott több kategória egymás után, egy
     * bejelentkezéssel felvehető, és minden mentést az oldal visszaigazol.
     *
     * <p><b>Előfeltételek:</b>
     * <ul>
     *   <li>a kezdőoldal betöltött, a felhasználó nincs bejelentkezve</li>
     *   <li>létezik az admin fiók (admin@practicesoftwaretesting.com / welcome01)</li>
     *   <li>a categories.csv legalább egy adatsort tartalmaz, és a benne szereplő
     *       szülőkategóriák léteznek</li>
     * </ul>
     *
     * <p><b>Tesztlépések:</b>
     * <ol>
     *   <li>Bejelentkezés adminként, majd Menü → Categories</li>
     *   <li>A categories.csv beolvasása (a fejléc sort és az üres sorokat kihagyja)</li>
     *   <li>Minden adatsorra: az Add Category űrlap megnyitása, szülőkategória, név és
     *       slug ("{slug}-{futásazonosító}") megadása, Save, a sikerüzenet megvárása</li>
     * </ol>
     *
     * <p><b>Elvárt eredmény:</b>
     * <ul>
     *   <li>a CSV legalább egy adatsort tartalmaz</li>
     *   <li>minden sor mentése után 10 másodpercen belül megjelenik a "Category saved!"
     *       üzenet; sikertelen sor esetén a teszt kiírja a hibás kategóriák nevét</li>
     * </ul>
     */
    @Test
    @DisplayName("TC-09: A categories.csv összes kategóriájának felvétele egymás után")
    void repeatedCategoryDataTest() throws Exception {

        RepeatedCategoryEntryPage page =
            new RepeatedCategoryEntryPage(driver);

        page.loginAndOpenCategories(
            "admin@practicesoftwaretesting.com",
            "welcome01"
        );

        InputStream input = getClass()
            .getClassLoader()
            .getResourceAsStream("categories.csv");

        assertTrue(input != null,
            "A categories.csv fájl nem található!");

        // Futásonként egyedi utótag a slugokhoz
        String runId = String.valueOf(System.currentTimeMillis());

        int processedRows = 0;
        List<String> failedCategories = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {

            String line;
            boolean header = true;

            while ((line = reader.readLine()) != null) {

                // Az első sor a fejléc
                if (header) {
                    header = false;
                    continue;
                }

                if (line.isBlank()) {
                    continue;
                }

                // A sor mezőkre bontása pontosvessző mentén. A -1 miatt az üres mezők
                // a sor végén is megmaradnak (pl. "Név;;" → 3 elem, nem 1)
                String[] data = line.split(";", -1);

                // A mezők a fájl oszlopsorrendjében, a szélső szóközök levágásával
                String name = data[0].trim();
                String category = data[1].trim();
                // A slugnak egyedinek kell lennie, ezért a futás azonosítóját (runId) hozzáfűzzük,
                // így a teszt többször is futtatható anélkül, hogy a korábbi slugokkal ütközne
                String slug = data[2].trim() + "-" + runId;

                page.openAddCategoryForm();
                page.addCategory(name, category, slug);
                processedRows++;

                // Csak a ténylegesen elmentett kategóriát számoljuk sikeresnek;
                // a következő sor előtt meg kell várni a mentés végét
                if (page.isCategorySavedDisplayed()) {
                    System.out.println("Sikeresen elmentve: " + name);
                } else {
                    failedCategories.add(name);
                }
            }
        }

        assertTrue(processedRows > 0, "A categories.csv nem tartalmaz adatsort!");

        assertTrue(
            failedCategories.isEmpty(),
            "Nem sikerült elmenteni ezeket a kategóriákat: " + failedCategories
        );

        System.out.println(
            "Összesen elmentett kategóriák: " + processedRows
        );
    }
}
