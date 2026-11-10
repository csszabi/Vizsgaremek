package utils;

/**
 * A tesztek közös beállításai és tesztadatai egy helyen.
 *
 * <p>A tesztelt oldal címe és a demóoldal beépített fiókjai itt vannak, így ha
 * bármelyik megváltozik, csak ezt a fájlt kell módosítani.
 */
public final class TestConfig {

	/** A tesztelt oldal kezdőlapja. */
	public static final String BASE_URL = "https://practicesoftwaretesting.com/";

	/** A demóoldal beépített admin fiókjának e-mail címe. */
	public static final String ADMIN_EMAIL = "admin@practicesoftwaretesting.com";

	/** A demóoldal beépített admin fiókjának jelszava. */
	public static final String ADMIN_PASSWORD = "welcome01";

	/** A demóoldal beépített vásárlói fiókjának e-mail címe (ehhez tartoznak számlák). */
	public static final String CUSTOMER_EMAIL = "customer@practicesoftwaretesting.com";

	/** A demóoldal beépített vásárlói fiókjának jelszava. */
	public static final String CUSTOMER_PASSWORD = "welcome01";

	private TestConfig() {
	}
}
