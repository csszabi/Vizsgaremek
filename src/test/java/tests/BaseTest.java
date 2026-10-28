package tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * Az összes tesztosztály közös ősosztálya.
 * 
 * Azért jött létre, hogy a böngésző indítása és bezárása (setup / teardown) egy
 * helyen legyen, ne kelljen minden tesztosztályban megismételni. Ugyanaz a
 * gondolat, mint a {@code BasePage} a Page Objecteknél.
 * 
 * Két környezetben fut:
 * 
 * Helyben (saját gépen): látható, teljes méretű Chrome ablakban.
 * GitHub Actions-ben: ott nincs képernyő, ezért "headless" (ablak nélküli)
 *  				   módban. A GitHub minden futásnál beállítja a CI környezeti változót,
 *                     ebből ismerjük fel, hogy ott futunk.
 */
public abstract class BaseTest {

	/** A tesztelt oldal kezdőlapja. */
	protected static final String BASE_URL = "https://practicesoftwaretesting.com/";

	/** A teszt böngészője; minden teszt előtt új példány indul. */
	protected WebDriver driver;

	/**
	 * Minden teszt előtt elindítja a Chrome-ot, és megnyitja a kezdőoldalt.
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
