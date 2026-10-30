# Practice Software Testing – automatizált UI tesztek

Selenium WebDriver + JUnit alapú, automatizált felhasználói felület (UI) tesztek a
[Practice Software Testing](https://practicesoftwaretesting.com/) demó webáruházhoz.
A tesztek helyben (Eclipse-ből vagy parancssorból) futtathatók, az eredményekről Allure
riport készül. GitHub Actions workflow is tartozik a projekthez, de ott a tesztelt oldal
Cloudflare-védelme miatt a tesztek nem érik el az oldalt
(részletek: [Ismert korlát](#ismert-korlát-cloudflare-védelem)).

## Használt technológiák

| Eszköz | Verzió | Szerepe |
|---|---|---|
| Java | 21 | programozási nyelv |
| Maven | 3.9+ | build és függőségkezelés |
| Selenium WebDriver | 4.51.0 | a böngésző vezérlése |
| WebDriverManager | 6.4.0 | a ChromeDriver automatikus letöltése |
| JUnit Jupiter | 6.1.3 | tesztkeretrendszer |
| Allure | 2.29.1 | tesztriport |
| Google Chrome | aktuális | a tesztek böngészője |

## Projektszerkezet

```
src/test/java/
├── tests/        tesztosztályok (mit ellenőrzünk)
│   └── BaseTest  közös ősosztály: böngésző indítása / bezárása
├── pages/        Page Object osztályok (hogyan érjük el az oldal elemeit)
│   └── BasePage  közös ősosztály: várakozások, kattintás, gépelés
└── utils/        segédosztályok (CSV olvasás, Cloudflare ellenőrző oldal kivárása)

src/test/resources/
├── categories.csv            tesztadatok az adatvezérelt teszthez
├── junit-platform.properties a tesztosztályok futási sorrendje (@Order)
└── allure.properties         az Allure eredmények helye
```

A projekt a **Page Object Model** mintát követi: a tesztek nem közvetlenül keresik az
oldal elemeit, hanem a `pages` csomag osztályain keresztül. Így ha az oldal felépítése
változik, csak egy helyen kell javítani.

## Tesztesetek

A tesztosztályok az alábbi sorrendben futnak (`@Order`):

| # | Tesztosztály | Mit ellenőriz |
|---|---|---|
| 1 | `RegistrationSuccessTest` | Új vásárlói fiók regisztrációja egyedi e-mail címmel |
| 2 | `LogonSuccessTest` | Admin bejelentkezés, utána betölt a Dashboard |
| 3 | `LogOffSuccessTest` | Kijelentkezés, utána a login oldal jelenik meg |
| 4 | `PagingProductsTest` | Terméklista lapozása az 1–5. oldal között és vissza |
| 5 | `DownloadDataTest` | Számla PDF letöltése vásárlói fiókkal |
| 6 | `AddDataTest` | Új kategória felvétele |
| 7 | `ModifyDataTest` | Kategória nevének és slugjának módosítása |
| 8 | `DeleteDataTest` | Kategória törlése |
| 9 | `RepeatedCategoryEntryTest` | Több kategória felvétele egymás után CSV fájlból (adatvezérelt) |
| 10 | `CategoryListTest` | A kategórialista megjelenik, benne a Hand Tools kategóriával |

Minden teszt önálló, nem függ más tesztek eredményétől. Az ütközések elkerülése
érdekében a tesztek egyedi adatokat használnak (időbélyeget tartalmazó e-mail cím
és slug), ezért többször egymás után is futtathatók.

### Ismert hiba

A **`DeleteDataTest`** jelenleg szándékosan elbukik. A törlés sikeres (megjelenik a
„Category deleted.” üzenet), de a lista újratöltésekor a böngésző kb. 2 percig a
gyorsítótárból adja vissza a régi listát, amelyben a törölt kategória még szerepel.
Ez az alkalmazás hibája, nem a teszté, ezért az ellenőrzés nincs megkerülve.

### Nem automatizált teszteset

**Adatkezelési nyilatkozat használata** – ehhez a tesztesethez nem készült automatizált
teszt, mert a tesztelt oldalon nincs ilyen lehetőség (nincs adatkezelési nyilatkozat,
amelyet a felhasználó megtekinthetne vagy elfogadhatna).

## Futtatás helyben

### Előfeltételek

- JDK 21
- Maven 3.9 vagy újabb
- Google Chrome (a hozzá illő ChromeDriver-t a WebDriverManager tölti le)
- internetkapcsolat (a tesztek az élő demóoldalon futnak)

### Eclipse-ből

- **Egy tesztosztály:** jobb klikk a tesztosztályon → *Run As → JUnit Test*
- **Az összes teszt:** jobb klikk a projekten → *Run As → Maven test*
- **Tesztek + riport:** jobb klikk a projekten → *Run As → Maven build...*, a *Goals* mezőbe:

  ```
  clean test allure:serve -Dmaven.test.failure.ignore=true
  ```

### Parancssorból

Az összes teszt:

```bash
mvn clean test
```

Egyetlen tesztosztály:

```bash
mvn test -Dtest=LogonSuccessTest
```

### Bemutató mód (lassítás)

A tesztek alapértelmezetten teljes sebességgel futnak. Bemutatóhoz bekapcsolható egy
lassítás, ilyenkor minden kattintás és gépelés előtt a megadott ideig (ezredmásodpercben)
várnak, hogy a futás szemmel követhető legyen:

```bash
mvn test -Dslowmo=800
```

Eclipse-ből futtatva a lassítás a run configuration *Arguments → VM arguments* mezőjében
adható meg (`-Dslowmo=800`). A lassítás csak a szemnek szól: az elemek megjelenésére
a tesztek mindenhol explicit várakozással (`WebDriverWait`) várnak.

## Allure riport

A tesztek futás közben a `target/allure-results` mappába írják az eredményeket.
Ebből a riport:

```bash
mvn allure:serve
```

Ez elkészíti a riportot és megnyitja a böngészőben. Csak a fájlok előállításához
(böngésző nélkül):

```bash
mvn allure:report
```

Ilyenkor a riport a `target/site/allure-maven-plugin/index.html` fájlba kerül.

Ha a riportot a bukott tesztekkel együtt szeretnéd egy lépésben elkészíteni, add hozzá a
`-Dmaven.test.failure.ignore=true` kapcsolót, különben a Maven a bukott teszt után megáll,
és a riport nem készül el.

## GitHub Actions

A tesztek GitHub Actions-ben is futnak. A workflow: [`.github/workflows/ui-tests.yml`](.github/workflows/ui-tests.yml)

**Mikor fut:** minden push és pull request után, valamint kézzel is indítható
(*Actions* fül → *UI tesztek* → *Run workflow*).

**Mit csinál:**

1. Java 21-et telepít, és ellenőrzi, hogy a GitHub gépe eléri-e a tesztelt oldalt
   (HTTP státusz, oldalcím; lásd lent, [Ismert korlát](#ismert-korlát-cloudflare-védelem)).
2. Lefuttatja a teszteket (`mvn clean test -Dslowmo=0`).
3. A futás oldalán összesítő táblázatot készít a tesztek eredményéről (JUnit riport).
4. Elkészíti az Allure riportot, és `allure-report` néven letölthető artifactként elmenti
   (14 napig érhető el).

A CI környezetben nincs képernyő, ezért a `BaseTest` a `CI` környezeti változóból
(amelyet a GitHub minden futásnál beállít) felismeri, hogy ott fut, és a Chrome-ot
**headless** (ablak nélküli) módban, 1920×1080-as mérettel indítja. A tesztkódon ehhez
nem kell változtatni. A Chrome az ubuntu-latest gépen előre telepítve van.

**Az eredmény megtekintése:** a GitHub repó **Actions** fülén nyisd meg az adott futást.
Az összesítő a futás oldalán látható, az Allure riport az oldal alján, az *Artifacts*
részből tölthető le. Kicsomagolás után az `index.html`-t érdemes a `allure open <mappa>`
paranccsal vagy egy helyi webszerverrel megnyitni, mert fájlból megnyitva a böngésző
nem tölti be az adatokat.

Ha bármelyik teszt elbukik, a futás piros lesz, de a riportok ettől még elkészülnek.

### Ismert korlát: Cloudflare-védelem

**GitHub Actions-ben jelenleg minden teszt elbukik, de nem a tesztek hibája miatt.**

A tesztelt oldal (practicesoftwaretesting.com) Cloudflare védelem mögött van, amely a
GitHub Actions adatközponti gépeiről érkező kéréseket automatizált forgalomnak tekinti.
Ezekre a valódi oldal helyett egy botellenőrző oldalt ad vissza, így a böngészőben meg sem
jelenik az alkalmazás, és a tesztek már az első lépésnél (pl. a *Sign in* gomb keresésénél)
elbuknak.

Ezt a workflow *„Tesztelt oldal elérhetőségének ellenőrzése”* lépése igazolja:

| Cím | Eredmény GitHub Actions-ből |
|---|---|
| `https://practicesoftwaretesting.com/` | **403**, `cf-mitigated: challenge`, oldalcím: *„Just a moment...”* |
| `https://api.practicesoftwaretesting.com/products` | **200**, rendes terméklista (az API nincs a védelem mögött) |

Saját gépről ugyanez az oldal 200-as státusszal, rendesen betölt, és a tesztek
– ugyanazokkal a headless beállításokkal is (`CI=true`) – lefutnak. A teszteket ezért
**helyben** kell futtatni (lásd: [Futtatás helyben](#futtatás-helyben)); a GitHub Actions
workflow a CI-beállítás bemutatására és a korlát dokumentálására szolgál.

**Kipróbált megoldás: az ellenőrzés kivárása.** A Cloudflare ellenőrző oldala bizonyos
esetekben emberi beavatkozás nélkül, néhány másodperc alatt magától továbbenged. Ennek
kihasználására készült a `utils/CloudflareWait` osztály: a `BaseTest` a kezdőoldal
megnyitása után meghívja, és ha az ellenőrző oldal látszik („Just a moment...” cím vagy
„Performing security verification” szöveg), legfeljebb 60 másodpercig várja, hogy eltűnjön;
ha beragad, újratölti az oldalt.

Az eredmény GitHub Actions-ben: **a Cloudflare 60 másodperc alatt sem engedte tovább a
böngészőt**, vagyis a GitHub gépeiről érkező headless Chrome-ot véglegesen blokkolja. A
tesztek ezért továbbra is elbuknak, de már egyértelmű hibaüzenettel:

```
A Cloudflare ellenőrző oldala 60 mp alatt sem engedett tovább (a tesztelt oldal blokkolja ezt a gépet)
```

Az osztály a projektben maradt: helyben nincs hatása (ott nincs ellenőrző oldal, azonnal
továbblép), CI-ben pedig a riportban is egyértelművé teszi a bukás okát.

A Cloudflare ellenőrzését a tesztek szándékosan nem kerülik meg. 

A `DeleteDataTest` ettől függetlenül, az [ismert alkalmazáshiba](#ismert-hiba) miatt
helyben is elbukik.

## Felhasznált tesztadatok

- **Admin és vásárlói fiók:** a demóoldal nyilvános, beépített tesztfiókjai.
- **`categories.csv`:** a `RepeatedCategoryEntryTest` bemenete, pontosvesszővel elválasztva,
  fejléc sorral (`name;parent_category;slug`). A teszt minden slug végére futásonként
  egyedi utótagot tesz.
