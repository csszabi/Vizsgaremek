package pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page Object az új vásárlói fiók regisztrációjához a Practice Software Testing oldalon.
 *
 * <p><b>Mit modellez:</b> a regisztrációs folyamatot és űrlapot.
 * Kezdőoldal → Sign in → Register your account → űrlap kitöltése → Register.
 * Sikeres regisztráció után az oldal a login oldalra (/auth/login) irányít.
 *
 * <p><b>Funkciói:</b>
 * <ul>
 *   <li>{@link #open()} – a kezdőoldal megnyitása</li>
 *   <li>{@link #register} – az űrlap kitöltése és elküldése</li>
 *   <li>{@link #isRegistrationSuccessful()} – a sikeres regisztráció ellenőrzése</li>
 *   <li>{@link #getRegistrationError()} – az oldal hibaüzenetének lekérdezése</li>
 * </ul>
 *
 * <p><b>Az űrlap szabályai</b>:
 * <ul>
 *   <li>születési dátum: ÉÉÉÉ-HH-NN formátum</li>
 *   <li>telefonszám: csak számjegyek</li>
 *   <li>jelszó: min. 8 karakter, kis- és nagybetű, szám és speciális karakter</li>
 *   <li>e-mail cím: egyedi, már regisztrált címmel a regisztráció hibát ad</li>
 * </ul>
 *
 * <p>Ország, irányítószám és házszám megadása után az oldal automatikusan kitöltheti
 * az utca, város és megye mezőket (cím-lekérdezés), ezért ezeket csak a lekérdezés
 * után írjuk felül.
 *
 * <p>A kattintások, gépelések és választások a {@link BasePage} segédmetódusain
 * keresztül történnek, így bemutató módban (-Dslowmo=...) lassítva futnak.
 */
public class RegistrationPage extends BasePage {

	private By signInButton = By.cssSelector("[data-test='nav-sign-in']");
	private By registrationLink = By.cssSelector("[data-test='register-link']");

	// Regisztrációs űrlap
	private By firstNameField = By.id("first_name");
	private By lastNameField = By.id("last_name");
	private By dobField = By.id("dob");
	private By countryField = By.cssSelector("[data-test='country']");
	private By postalCodeField = By.id("postal_code");
	private By houseNumberField = By.id("house_number");
	private By streetField = By.id("street");
	private By cityField = By.id("city");
	private By stateField = By.id("state");
	private By phoneField = By.id("phone");
	private By emailField = By.id("email");
	private By passwordField = By.id("password");
	private By registrationButton = By.cssSelector("[data-test='register-submit']");

	// A cím-lekérdezés közben megjelenő "töltés" jelzés
	private By postcodeLookupSpinner = By.cssSelector("[role='status'].alert-info");

	// Sikertelen regisztrációkor megjelenő hibaüzenet (pl. "Email is already in use.")
	private By registerError = By.cssSelector("[data-test='register-error']");

	/** Explicit várakozás (max. 10 mp) az ellenőrzésekhez és az oldalállapotokhoz. */
	private WebDriverWait wait;

	/**
	 * @param driver a teszt által elindított WebDriver
	 */
	public RegistrationPage(WebDriver driver) {
		super(driver);
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	}

	/**
	 * Megnyitja a tesztelt oldal kezdőlapját; innen indul a regisztráció.
	 */
	public void open() {

		driver.get("https://practicesoftwaretesting.com/");
	}

	/**
	 * Kitölti és elküldi a regisztrációs űrlapot.
	 *
	 * <p>A kezdőoldalról, kijelentkezett állapotból kell indítani: a Sign in, majd a
	 * Register your account linkkel nyitja meg az űrlapot. Országnak mindig
	 * Magyarországot ("Hungary") választja. Az irányítószám és a házszám után megvárja
	 * az automatikus cím-lekérdezést, és csak utána írja be az utcát, várost, megyét,
	 * hogy a lekérdezés ne írja felül őket.
	 *
	 * <p>Az eredményt az {@link #isRegistrationSuccessful()} és a
	 * {@link #getRegistrationError()} metódusokkal lehet ellenőrizni.
	 *
	 * @param firstName keresztnév
	 * @param lastName vezetéknév
	 * @param dob születési dátum ÉÉÉÉ-HH-NN formátumban
	 * @param postalCode irányítószám (HU: 4 számjegy)
	 * @param houseNumber házszám
	 * @param street utca
	 * @param city város
	 * @param state megye
	 * @param phone telefonszám, csak számjegyek
	 * @param email e-mail cím; egyedinek kell lennie
	 * @param password jelszó az osztály leírásában szereplő szabályok szerint
	 */
	public void register(String firstName, String lastName, String dob, String postalCode, String houseNumber, String street, String city, String state, String phone, String email, String password) {

		waitAndClick(signInButton);
		waitAndClick(registrationLink);

		type(firstNameField, firstName);
		type(lastNameField, lastName);
		type(dobField, dob);

		selectByText(countryField, "Hungary");

		type(postalCodeField, postalCode);
		type(houseNumberField, houseNumber);

		// Megvárjuk, hogy az automatikus cím-lekérdezés lefusson, különben
		// utólag felülírhatja az általunk beírt utcát, várost, megyét
		waitForPostcodeLookup();

		replaceText(streetField, street);
		replaceText(cityField, city);
		replaceText(stateField, state);

		type(phoneField, phone);
		type(emailField, email);
		type(passwordField, password);

		click(registrationButton);
	}

	/**
	 * Ellenőrzi, hogy a regisztráció sikeres volt-e, vagyis az oldal átirányított
	 * a login oldalra (/auth/login). A {@link #register} után kell hívni.
	 *
	 * @return true, ha 10 mp-en belül a login oldal nyílt meg; különben false
	 */
	public boolean isRegistrationSuccessful() {
		try {
			return wait.until(ExpectedConditions.urlContains("/auth/login"));

		} catch (org.openqa.selenium.TimeoutException e) {
			return false;
		}
	}

	/**
	 * Visszaadja a regisztrációs űrlap alatt megjelenő hibaüzenetet
	 * (pl. "Email is already in use."). Sikertelen regisztráció esetén a hiba okának
	 * kiírására szolgál.
	 *
	 * @return a hibaüzenet szövege, vagy üres szöveg, ha nincs hibaüzenet
	 */
	public String getRegistrationError() {
		return driver.findElements(registerError).isEmpty() ? "" : getText(registerError);
	}

	
	//Megvárja az irányítószám alapján induló automatikus cím-lekérdezés végét.
	private void waitForPostcodeLookup() {
		try {
			new WebDriverWait(driver, Duration.ofSeconds(2))
				.until(ExpectedConditions.presenceOfElementLocated(postcodeLookupSpinner));
		} catch (org.openqa.selenium.TimeoutException e) {
			// Nem indult lekérdezés, vagy már le is futott
		}
		wait.until(ExpectedConditions.invisibilityOfElementLocated(postcodeLookupSpinner));
	}

	/**
	 * Lecseréli egy beviteli mező tartalmát: kijelöli a meglévő szöveget,törli, 
	 * majd beírja az újat.
	 *
	 * <p>A cím-lekérdezés által kitöltött mezőknél kell, mert a sima gépelés a meglévő
	 * szöveg mögé írna. A {@code clear()} nem jó, mert nem vált ki input eseményt,
	 * így az űrlap a régi értéket tartaná meg. A sendKeys()-t közvetlenül
	 * hívja, ezért itt külön kell a bemutató módú {@link #pause()}.
	 *
	 * @param locator a beviteli mező lokátora
	 * @param text az új szöveg
	 */
	private void replaceText(By locator, String text) {
		pause();
		WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
		input.click();
		input.sendKeys(Keys.CONTROL, "a");
		input.sendKeys(Keys.BACK_SPACE);
		input.sendKeys(text);
	}

}
