package tests;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import io.github.bonigarcia.wdm.WebDriverManager;
import utils.CloudflareWait;

/**
 * Az összes tesztosztály közös ősosztálya.
 *
 * <p><b>Miért jött létre?:</b> hogy a böngésző indítása és bezárása (setup / teardown)
 * egy helyen legyen, ne kelljen minden tesztosztályban megismételni. Ugyanaz a
 * gondolat, mint a {@code BasePage} a Page Objecteknél.
 *
 * <p><b>Keretrendszer-funkciói:</b>
 * <ul>
 *   <li>minden teszt előtt új Chrome-ot indít, és megnyitja a tesztelt oldal kezdőlapját
 *       ({@link #startBrowser()})</li>
 *   <li>minden teszt után bezárja a böngészőt ({@link #closeBrowser()})</li>
 *   <li>összeállítja a Chrome beállításait, amelyeket a leszármazottak kiegészíthetnek
 *       ({@link #createChromeOptions()})</li>
 *   <li>felismeri, hogy CI környezetben fut-e ({@link #isRunningOnCi()})</li>
 * </ul>
 *
 * <p><b>Két környezetben fut:</b>
 * <ul>
 *   <li><b>Helyben</b> (saját gépen): látható, teljes méretű Chrome ablakban.</li>
 *   <li><b>GitHub Actions-ben:</b> ott nincs képernyő, ezért "headless" (ablak nélküli)
 *       módban. A GitHub minden futásnál beállítja a CI környezeti változót, ebből
 *       ismerjük fel, hogy ott futunk.</li>
 * </ul>
 */
public abstract class BaseTest {

	/** A tesztelt oldal kezdőlapja. */
	protected static final String BASE_URL = "https://practicesoftwaretesting.com/";

	/** A teszt böngészője; minden teszt előtt új példány indul. */
	protected WebDriver driver;

	/**
	 * Minden teszt előtt elindítja a Chrome-ot, és megnyitja a kezdőoldalt.
	 *
	 * <p>Helyben teljes méretűre nagyítja az ablakot. A kezdőoldal megnyitása után
	 * a {@link CloudflareWait} megvárja, amíg a Cloudflare esetleges ellenőrző oldala
	 * továbbenged (helyben ilyen nincs, ott azonnal továbblép).
	 * (A leszármazott osztályok saját @BeforeEach metódusai ez után futnak.)
	 */
	@BeforeEach
	void startBrowser() {

		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver(createChromeOptions());

		// Headless módban nincs ablak, amit maximalizálni lehetne;
		// ott a méretet a createChromeOptions() állítja be
		if (!isRunningOnCi()) {
			driver.manage().window().maximize();
		}
		
		driver.get(BASE_URL);

		// GitHub Actions-ben a Cloudflare ellenőrző oldalt adhat; megvárjuk, amíg továbbenged.
		// Helyben nincs ellenőrzés, ott azonnal továbblép.
		CloudflareWait.waitForVerification(driver, Duration.ofSeconds(60));
	}

	/**
	 * Minden teszt után bezárja a böngészőt.
	 * (A leszármazott osztályok saját @AfterEach metódusai ez előtt futnak.)
	 */
	@AfterEach
	void closeBrowser() {
		if (driver != null) {
			driver.quit();
		}
	}

	/**
	 * Összeállítja a Chrome indítási beállításait. A leszármazott osztályok
	 * felülírhatják, ha további beállítás kell (pl. a letöltési teszt saját
	 * letöltési mappát állít be); ilyenkor először a super.createChromeOptions()-t
	 * kell meghívniuk.
	 *
	 * @return a Chrome beállításai
	 */
	protected ChromeOptions createChromeOptions() {

		ChromeOptions options = new ChromeOptions();

		if (isRunningOnCi()) {
			options.addArguments(
				"--headless=new",               // ablak nélküli mód
				"--window-size=1920,1080",      // fix képernyőméret a maximalizálás helyett
				"--no-sandbox",                 // CI-gépeken szokásos, stabilabb indulás
				"--disable-dev-shm-usage"       // a CI-gépek kis megosztott memóriája miatt
			);
		}

		return options;
	}

	/**
	 * @return true, ha a teszt CI környezetben (pl. GitHub Actions) fut
	 */
	protected static boolean isRunningOnCi() {
		return System.getenv("CI") != null;
	}
}
