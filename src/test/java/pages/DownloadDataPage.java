package pages;

import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page Object egy számla PDF letöltéséhez a Practice Software Testing oldalon,
 * vásárlói (customer) fiókkal.
 * 
 * Lefedett folyamat: bejelentkezés → My account → Invoices → egy számla
 * Details oldala → Download PDF.
 * 
 * Fontos: a Download PDF gomb addig le van tiltva (disabled), amíg a szerver el nem
 * készíti a PDF-et (a háttérben futó PDF-készítés "COMPLETED" állapotba nem kerül).
 * Az oldal ezt az állapotot 20 másodpercenként kérdezi le. Ha egy számlához a
 * PDF-készítés el sem indult, a gomb soha nem lesz engedélyezett. Ezért a teszt nem
 * csak az első számlát nézi meg, hanem sorban többet, és az első olyat tölti le,
 * amelyiknél a PDF elkészült.
 * 
 * A letöltés a böngésző saját letöltéseként történik, ezért a sikerességét a
 * letöltési mappában megjelenő PDF fájllal lehet ellenőrizni.
 * 
 * A kattintások és gépelések a {@link BasePage} segédmetódusain keresztül
 * történnek, így bemutató módban (-Dslowmo=...) lassítva futnak.
 */
public class DownloadDataPage extends BasePage {

	/** Legfeljebb ennyi számlát próbál végig, mielőtt feladja. */
	private static final int MAX_INVOICES_TO_TRY = 5;

	/**
	 * Ennyi ideig vár számlánként a Download PDF gomb engedélyezésére.
	 * Az oldal azonnal, majd 20 másodpercenként kérdezi le a PDF állapotát,
	 * így 25 mp alatt két lekérdezés is lefut.
	 */
	private static final Duration DOWNLOAD_READY_TIMEOUT = Duration.ofSeconds(25);

	// Bejelentkezés
	private By signInButton = By.cssSelector("[data-test='nav-sign-in']");
	private By emailAddressField = By.id("email");
	private By passwordField = By.id("password");
	private By loginButton = By.cssSelector("[data-test='login-submit']");

	// My account → Invoices
	private By invoicesButton = By.cssSelector("[data-test='nav-invoices']");

	// A számlalista Details gombjai (href: /account/invoices/{id})
	private By invoiceDetailLinks = By.cssSelector("a[href^='/account/invoices/']");

	// Számla részletei
	private By downloadPdfButton = By.cssSelector("[data-test='download-invoice']");

	// Sikertelen letöltéskor megjelenő hibaüzenet ("Document not created. Try again later.")
	private By downloadErrorMessage = By.cssSelector("div[role='alert'].alert-danger");

	/** Explicit várakozás (max. 10 mp) az átirányításhoz és a lista betöltéséhez. */
	private WebDriverWait wait;

	/** Hosszabb várakozás a fájl letöltésére. */
	private WebDriverWait longWait;

	/** Hány számlát vizsgált meg a legutóbbi {@link #downloadData} hívás. */
	private int triedInvoices;

	/**
	 * @param driver a teszt által elindított WebDriver
	 */
	public DownloadDataPage(WebDriver driver) {
		super(driver);
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		longWait = new WebDriverWait(driver, Duration.ofSeconds(60));
	}

	/** Megnyitja az oldal kezdőlapját. */
	public void open() {

		driver.get("https://practicesoftwaretesting.com/");
	}

	/**
	 * Bejelentkezik, majd sorban megnyitja a számlákat (legfeljebb
	 * {@value #MAX_INVOICES_TO_TRY} darabot), és az első olyannál, amelyikhez a PDF
	 * elkészült, rákattint a Download PDF gombra.
	 * A kezdőoldalról, kijelentkezett állapotból kell indítani.
	 *
	 * @param email vásárló e-mail címe
	 * @param password vásárló jelszava
	 * @return true, ha elindult egy letöltés; false, ha egyik vizsgált számlához
	 *         sem készült el a PDF
	 */
	public boolean downloadData (String email, String password) {

		// Bejelentkezés
		waitAndClick(signInButton);

		type(emailAddressField, email);
		type(passwordField, password);

		waitAndClick(loginButton);

		// Megvárjuk a bejelentkezés utáni átirányítást (teljes oldal-újratöltés)
		wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/auth/login")));

		// Számlák menü megnyitása (bejelentkezés után a My account oldalra kerülünk)
		waitAndClick(invoicesButton);

		// Megvárjuk a számlalistát, és összegyűjtjük az első néhány számla címét.
		// (A címeket előre kigyűjtjük, mert a részletek oldalra lépve a lista elemei
		// eltűnnek az oldalról.)
		wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(invoiceDetailLinks, 0));
		List<String> invoiceUrls = driver.findElements(invoiceDetailLinks).stream()
				.map(link -> link.getDomProperty("href"))
				.limit(MAX_INVOICES_TO_TRY)
				.toList();

		triedInvoices = 0;

		for (String invoiceUrl : invoiceUrls) {

			// Számla részleteinek megnyitása
			pause();
			driver.get(invoiceUrl);
			triedInvoices++;

			// Ha a PDF elkészült, letöltjük; ha nem, jöhet a következő számla
			if (isDownloadReady()) {
				waitAndClick(downloadPdfButton);
				return true;
			}
		}

		return false;
	}

	/**
	 * @return hány számlát vizsgált meg a legutóbbi {@link #downloadData} hívás
	 */
	public int getTriedInvoices() {
		return triedInvoices;
	}

	/**
	 * Megvárja, hogy a megnyitott számlán a Download PDF gomb engedélyezett legyen,
	 * vagyis a szerver elkészítse a PDF-et.
	 *
	 * @return true, ha {@link #DOWNLOAD_READY_TIMEOUT} időn belül engedélyezett lett; különben false
	 */
	private boolean isDownloadReady() {
		try {
			new WebDriverWait(driver, DOWNLOAD_READY_TIMEOUT)
				.until(ExpectedConditions.elementToBeClickable(downloadPdfButton));
			return true;

		} catch (org.openqa.selenium.TimeoutException e) {
			return false;
		}
	}

	/**
	 * Megvárja, hogy a letöltési mappában megjelenjen egy teljesen letöltött PDF fájl.
	 * (A Chrome a letöltés közben .crdownload kiterjesztést használ, azt nem fogadjuk el.)
	 *
	 * @param downloadDir a böngészőben beállított letöltési mappa
	 * @return true, ha 60 mp-en belül megjelent egy .pdf fájl; különben false
	 */
	public boolean isFileDownloaded(Path downloadDir) {
		try {
			return longWait.until(d -> {
				// A letöltési mappa .pdf végű fájljai (kis- és nagybetűtől függetlenül)
				File[] pdfs = downloadDir.toFile().listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));
				// A listFiles() null-t ad, ha a mappa nem létezik vagy nem olvasható,
				// ezért ezt is ellenőrizni kell. Akkor sikeres, ha van legalább egy PDF.
				return pdfs != null && pdfs.length > 0;
			});

		} catch (org.openqa.selenium.TimeoutException e) {
			return false;
		}
	}

	/**
	 * Ellenőrzi, hogy megjelent-e a letöltési hibaüzenet.
	 *
	 * @return true, ha a hibaüzenet látható
	 */
	public boolean isDownloadErrorDisplayed() {
		return !driver.findElements(downloadErrorMessage).isEmpty()
				&& driver.findElement(downloadErrorMessage).isDisplayed();
	}

}
