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
 * Lefedett folyamat: kezdőoldal → Sign in → Register your account → űrlap kitöltése
 * → Register. Sikeres regisztráció után az oldal a login oldalra (/auth/login) irányít.
 * 
 * Az űrlap szabályai (az oldal forráskódja alapján):
 * 
 * születési dátum: ÉÉÉÉ-HH-NN formátum
 * telefonszám: csak számjegyek
 * jelszó: min. 8 karakter, kis- és nagybetű, szám és speciális karakter
 * e-mail cím: egyedi, már regisztrált címmel a regisztráció hibát ad
 * 
 * Ország + irányítószám + házszám megadása után az oldal automatikusan kitöltheti
 * az utca, város és megye mezőket (cím-lekérdezés), ezért ezeket csak a lekérdezés
 * után írjuk felül.
 *
 * A kattintások, gépelések és választások a {@link BasePage} segédmetódusain
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

	/** Megnyitja az oldal kezdőlapját. */
	public void open() {

		driver.get("https://practicesoftwaretesting.com/");
	}

	/**
	 * Kitölti és elküldi a regisztrációs űrlapot. Országnak mindig Magyarországot választja.
	 * A kezdőoldalról, kijelentkezett állapotból kell indítani.
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
	 * @param email e-mail cím, egyedinek kell lennie
	 * @param password jelszó a fenti szabályok szerint
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
	 * a login oldalra.
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
	 * Visszaadja a regisztrációs hibaüzenetet, ha van.
	 *
	 * @return a hibaüzenet szövege, vagy üres szöveg, ha nincs hiba
	 */
	public String getRegistrationError() {
		return driver.findElements(registerError).isEmpty() ? "" : getText(registerError);
	}

	/**
	 * Megvárja a cím-lekérdezés végét. A lekérdezés 300 ms késleltetéssel indul,
	 * ezért először rövid ideig várunk a jelzés megjelenésére (ha nem jelenik meg,
	 * az sem hiba), majd az eltűnésére.
	 */
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
	 * Lecseréli egy beviteli mező tartalmát.
	 * Közvetlenül a sendKeys()-t hívja, ezért itt külön kell a bemutató módú pause().
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
