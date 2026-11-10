package utils;

import java.time.Duration;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * A Cloudflare biztonsági ellenőrző oldalának kezelése.
 *
 * A tesztelt oldal Cloudflare mögött van. A GitHub Actions gépeiről érkező
 * kérésekre a valódi oldal helyett időnként egy ellenőrző oldal jön
 * ("Just a moment...", "Performing security verification"). Ez emberi
 * beavatkozás nélkül, néhány másodperc alatt magától továbbengedhet.
 * Ez az osztály felismeri ezt az oldalt, és megvárja, amíg eltűnik.
 */
public final class CloudflareWait {

	private static final String CHALLENGE_TITLE = "Just a moment";
	private static final String CHALLENGE_BODY = "Performing security verification";

	/** Ennyi ideig vár egy próbálkozásnál, mielőtt újratölti az oldalt. */
	private static final Duration ATTEMPT_TIMEOUT = Duration.ofSeconds(8);

	private CloudflareWait() {
	}

	/**
	 * Megnézi, hogy a böngészőben éppen a Cloudflare ellenőrző oldala látszik-e
	 * (a cím vagy az oldal szövege alapján).
	 *
	 * @param driver a teszt böngészője
	 * @return true, ha a böngészőben éppen a Cloudflare ellenőrző oldala látszik
	 */
	public static boolean isChallengePage(WebDriver driver) {
		try {
			String title = driver.getTitle();
			if (title != null && title.contains(CHALLENGE_TITLE)) {
				return true;
			}
			Object body = ((JavascriptExecutor) driver)
					.executeScript("return document.body ? document.body.innerText : '';");
			return body != null && body.toString().contains(CHALLENGE_BODY);
		} catch (WebDriverException e) {
			// Oldalváltás közben a lekérdezés hibát dobhat; ilyenkor nem tekintjük ellenőrző oldalnak
			return false;
		}
	}

	/**
	 * Ha az ellenőrző oldal látszik, megvárja, amíg a Cloudflare továbbenged.
	 * Ha az oldal beragad, újratölti. Ha az ellenőrzés a megadott időn belül sem
	 * ér véget, egyértelmű hibaüzenettel leállítja a tesztet.
	 *
	 * @param driver a teszt böngészője
	 * @param timeout ennyi ideig próbálkozik összesen
	 */
	public static void waitForVerification(WebDriver driver, Duration timeout) {
		if (!isChallengePage(driver)) {
			return;
		}

		long deadline = System.nanoTime() + timeout.toNanos();
		while (System.nanoTime() < deadline) {
			try {
				new WebDriverWait(driver, ATTEMPT_TIMEOUT).until(d -> !isChallengePage(d));
				return;
			} catch (TimeoutException e) {
				// Beragadt az ellenőrzés: újratöltéssel újraindítjuk
				driver.navigate().refresh();
			}
		}

		throw new TimeoutException("A Cloudflare ellenőrző oldala " + timeout.toSeconds()
				+ " mp alatt sem engedett tovább (a tesztelt oldal blokkolja ezt a gépet)");
	}
}