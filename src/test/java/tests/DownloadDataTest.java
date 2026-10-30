package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import pages.DownloadDataPage;

/**
 * Adat (számla PDF) letöltésének tesztje a Practice Software Testing oldalon.
 *
 * <p><b>Mit tesztel?:</b> egy bejelentkezett vásárló le tudja-e tölteni egy korábbi
 * számlája PDF-jét a My account → Invoices → Details oldalról.
 *
 * <p><b>Miért jött létre?:</b> a letöltés funkció ellenőrzésére. A letöltés sikerét a
 * teszt fájlszinten ellenőrzi: a Chrome egy erre létrehozott ideiglenes mappába tölt
 * le, és a teszt megvárja, hogy ott megjelenjen a PDF fájl.
 *
 * <p><b>Előfeltételek:</b>
 * <ul>
 *   <li>a tesztelt oldal elérhető, a felhasználó nincs bejelentkezve</li>
 *   <li>létezik a demóoldal beépített vásárlói fiókja
 *       (customer@practicesoftwaretesting.com), amelyhez már tartoznak számlák,
 *       ezért nem kell előtte tesztadatot létrehozni</li>
 *   <li>az első 5 számla közül legalább egyhez a szerver már elkészítette a PDF-et</li>
 * </ul>
 *
 * <p>A PDF-et a szerver a háttérben készíti, és nem minden számlához készül el (pl. a
 * demóoldal adatbázisának visszaállítása után). Ezért a teszt sorban több számlát
 * is megnéz, és az első olyat tölti le, amelyikhez a PDF már elkészült.
 *
 * <p>A böngésző indítását és bezárását a {@link BaseTest} végzi; ez az osztály csak
 * a letöltési mappa beállításával egészíti ki.
 */
@Order(5)
@DisplayName("Számla letöltése")
class DownloadDataTest extends BaseTest {

	/** Ideiglenes letöltési mappa, minden teszthez új. */
	Path downloadDir;

	/**
	 * A közös Chrome beállításokat kiegészíti: létrehoz egy ideiglenes letöltési
	 * mappát, és beállítja, hogy a Chrome kérdezés nélkül ide mentse a letöltéseket.
	 * A {@link BaseTest} a böngésző indításakor hívja meg.
	 */
	@Override
	protected ChromeOptions createChromeOptions() {

		// Először a BaseTest közös beállításai (headless mód, ablakméret stb.),
		// ezekhez adjuk hozzá alább a letöltési mappát
		ChromeOptions options = super.createChromeOptions();

		try {
			downloadDir = Files.createTempDirectory("invoice-download");
		} catch (IOException e) {
			throw new UncheckedIOException("Nem sikerült létrehozni a letöltési mappát", e);
		}

		// Chrome letöltési beállításai:
		// - a letöltött fájlok a downloadDir mappába kerüljenek (onnan ellenőrzi a teszt a PDF-et),
		// - a böngésző ne kérdezze meg, hova mentse a fájlt, mert az ablakot a Selenium nem tudja kezelni
		Map<String, Object> prefs = new HashMap<>();
		prefs.put("download.default_directory", downloadDir.toString());
		prefs.put("download.prompt_for_download", false);
		options.setExperimentalOption("prefs", prefs);

		return options;
	}

	/** Minden teszt után törli az ideiglenes letöltési mappát. */
	@AfterEach
	void deleteDownloadDir() {
		if (downloadDir == null) {
			return;
		}

		File[] files = downloadDir.toFile().listFiles();
		if (files != null) {
			for (File file : files) {
				file.delete();
			}
		}
		downloadDir.toFile().delete();
	}

	/**
	 * <b>TC-05 – Számla PDF letöltése</b>
	 *
	 * <p><b>Tesztfeltétel:</b> bejelentkezett vásárló egy korábbi számlájának PDF-jét le
	 * tudja tölteni, és a fájl ténylegesen megérkezik a gépre.
	 *
	 * <p><b>Előfeltételek:</b>
	 * <ul>
	 *   <li>a kezdőoldal betöltött, a felhasználó nincs bejelentkezve</li>
	 *   <li>létezik a vásárlói fiók (customer@practicesoftwaretesting.com / welcome01),
	 *       amelyhez számlák tartoznak</li>
	 *   <li>az első 5 számla közül legalább egyhez elkészült a PDF</li>
	 *   <li>a Chrome letöltési mappája egy üres, ideiglenes mappa (a teszt állítja be)</li>
	 * </ul>
	 *
	 * <p><b>Tesztlépések:</b>
	 * <ol>
	 *   <li>Bejelentkezés vásárlóként (Sign in → e-mail, jelszó → Login)</li>
	 *   <li>Invoices menüpont: a számlalista megnyitása</li>
	 *   <li>Sorban az első legfeljebb 5 számla Details oldalának megnyitása; mindegyiknél
	 *       legfeljebb 25 mp várakozás, hogy a Download PDF gomb engedélyezett legyen</li>
	 *   <li>Az első engedélyezett Download PDF gombra kattintás</li>
	 * </ol>
	 *
	 * <p><b>Elvárt eredmény:</b> valamelyik számlánál elindul a letöltés, és 60 másodpercen
	 * belül egy teljesen letöltött .pdf fájl jelenik meg a letöltési mappában.
	 */
	@Test
	@DisplayName("TC-05: Számla PDF letöltése vásárlói fiókkal")
	void downloadDataTest () {

		DownloadDataPage downloadDataPage = new DownloadDataPage(driver);
		boolean downloadStarted = downloadDataPage.downloadData("customer@practicesoftwaretesting.com", "welcome01");

		// Ha egyik vizsgált számlához sem készült el a PDF, az a demóoldal szerverének
		// állapotán múlik (a PDF-készítés el sem indult), nem a tesztkódon
		assertTrue(
			    downloadStarted,
			    "Egyik megvizsgált számlához (" + downloadDataPage.getTriedInvoices() + " db) sem készült el"
			    + " a PDF, ezért a Download PDF gomb végig letiltva maradt."
			);

		boolean downloaded = downloadDataPage.isFileDownloaded(downloadDir);

		assertTrue(
			    downloaded,
			    "A számla PDF nem töltődött le!"
			    + (downloadDataPage.isDownloadErrorDisplayed() ? " Az oldal hibát jelzett: Document not created." : "")
			);
	}
}
