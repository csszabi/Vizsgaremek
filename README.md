# Practice Software Testing – automatizált UI tesztek

Selenium WebDriver + JUnit alapú, automatizált felhasználói felület (UI) tesztek a
[Practice Software Testing](https://practicesoftwaretesting.com/) (Toolshop) demó
webáruházhoz. A projekt 11 tesztosztályban **14 tesztesetet** tartalmaz – pozitív és
negatív eseteket is –, amelyek a webáruház legfontosabb felhasználói és adminisztrátori
folyamatait ellenőrzik a böngészőben.

A tesztek helyben (Eclipse-ből vagy parancssorból) futtathatók, az eredményekről
automatikusan Allure riport készül. GitHub Actions workflow is tartozik a projekthez,
de ott a tesztelt oldal Cloudflare-védelme miatt a tesztek nem érik el az oldalt
(részletek: [Cloudflare-védelem](#cloudflare-védelem-github-actions)).

## Használt technológiák

| Eszköz | Verzió | Szerepe |
|---|---|---|
| Java | 21 | programozási nyelv |
| Maven (Maven Wrapperrel) | 3.10.0 | build és függőségkezelés |
| Selenium WebDriver | 4.51.0 | a böngésző vezérlése |
| WebDriverManager | 6.4.0 | a ChromeDriver automatikus letöltése |
| JUnit Jupiter | 6.1.3 | tesztkeretrendszer |
| Allure | 2.29.1 | tesztriport |
| Google Chrome | aktuális | a tesztek böngészője |

## Projektszerkezet

```
src/test/java/
├── tests/        tesztosztályok (mit ellenőrzünk)
│   └── BaseTest  közös ősosztály: böngésző indítása / bezárása, Chrome-beállítások
├── pages/        Page Object osztályok (hogyan érjük el az oldal elemeit)
│   └── BasePage  közös ősosztály: bejelentkezés, menü, várakozások, kattintás, gépelés
└── utils/
    ├── TestConfig      a tesztelt oldal címe és a tesztfiókok egy helyen
    └── CloudflareWait  a Cloudflare ellenőrző oldalának kivárása

src/test/resources/
├── categories.csv            tesztadatok az adatvezérelt teszthez
├── junit-platform.properties a tesztosztályok futási sorrendje (@Order)
└── allure.properties         az Allure eredmények helye

src/test/allure/
├── environment.properties    az Allure riport környezeti adatai
└── categories.json           a bukások csoportosítása az Allure riportban

mvnw, mvnw.cmd                Maven Wrapper indítószkript (Linux/macOS, ill. Windows)
.mvn/wrapper/                 a Wrapper beállításai (a használt Maven verzió)
.github/workflows/            GitHub Actions workflow
docs/                         vezetői tesztjelentés (Markdown és PDF)
```

A projekt a **Page Object Model** mintát követi: a tesztek nem közvetlenül keresik az
oldal elemeit, hanem a `pages` csomag osztályain keresztül. Így ha az oldal felépítése
változik, csak egy helyen kell javítani. A közös műveletek (kezdőoldal megnyitása,
bejelentkezés, menük) a `BasePage`-ben, a tesztadatok (oldal címe, fiókok) a
`TestConfig`-ban vannak, egy helyen.

## Tesztesetek

A tesztosztályok az alábbi sorrendben futnak (`@Order`). Minden teszteset TC-azonosítót
kapott, amely a tesztmetódus Javadocjában és a `@DisplayName`-ben (így az Eclipse JUnit
nézetében és az Allure riportban) is látszik:

| # | Tesztosztály | TC | Mit ellenőriz |
|---|---|---|---|
| 1 | `RegistrationTest` | TC-01.1 | Sikeres regisztráció új, egyedi e-mail címmel; utána a login oldal jelenik meg |
| | | TC-01.2 | *Negatív:* regisztráció már regisztrált e-mail címmel – hibaüzenet, az oldal a regisztrációs űrlapon marad |
| 2 | `LoginTest` | TC-02.1 | Admin bejelentkezés, utána betölt a Dashboard |
| | | TC-02.2 | Vásárlói bejelentkezés, utána betölt a My account oldal |
| | | TC-02.3 | *Negatív:* bejelentkezés nem létező fiókkal – „Invalid email or password”, az oldal a login oldalon marad |
| 3 | `LogOffSuccessTest` | TC-03 | Kijelentkezés, utána a login oldal jelenik meg |
| 4 | `PagingProductsTest` | TC-04 | Terméklista lapozása az 1–5. oldal között és vissza |
| 5 | `DownloadDataTest` | TC-05 | Számla PDF letöltése vásárlói fiókkal (a fájl ténylegesen megérkezik) |
| 6 | `AddDataTest` | TC-06 | Új kategória felvétele |
| 7 | `ModifyDataTest` | TC-07 | Kategória nevének és slugjának módosítása |
| 8 | `DeleteDataTest` | TC-08 | Kategória törlése |
| 9 | `RepeatedCategoryEntryTest` | TC-09 | Több kategória felvétele egymás után CSV fájlból (adatvezérelt) |
| 10 | `CategoryListTest` | TC-10 | A kategórialista megjelenik, benne a Hand Tools kategóriával |
| 11 | `PrivacyPolicyTest` | TC-11 | Az adatkezelési nyilatkozat (Privacy Policy) megnyílik a lábléc linkjéről |

Minden teszteset részletes leírása (tesztfeltétel, előfeltételek, tesztlépések, elvárt
eredmény) a tesztmetódus Javadocjában található.

Minden teszt önálló, nem függ más tesztek eredményétől. Az ütközések elkerülése
érdekében a tesztek egyedi adatokat használnak (időbélyeget tartalmazó e-mail cím
és slug), ezért többször egymás után is futtathatók.

### Negatív tesztesetek

- **TC-01.2 – sikertelen regisztráció:** a demóoldal beépített vásárlói fiókjának e-mail
  címével próbál regisztrálni, és a *„A customer with this email address already exists.”*
  hibaüzenetet várja.
- **TC-02.3 – sikertelen bejelentkezés:** szándékosan **minden futáskor egyedi, nem létező**
  e-mail címmel és hibás jelszóval próbálkozik. Ha egy valódi fiókba lépne be többször
  rossz jelszóval, az oldal zárolná a fiókot (lásd: [Zárolt vásárlói fiók](#zárolt-vásárlói-fiók)),
  ami a többi tesztet is elrontaná.

### Adatkezelési nyilatkozat

Az **„Adatkezelési nyilatkozat használata”** tesztesetet a `PrivacyPolicyTest` (TC-11) fedi
le: a nyilatkozat (Privacy Policy) minden oldal láblécéből elérhető, és a teszt ellenőrzi,
hogy megnyílik-e, és a nyilatkozat szövege jelenik-e meg. Elfogadandó nyilatkozat
(pl. jelölőnégyzet a regisztrációnál) az oldalon nincs, ezért a teszt a nyilatkozat
elérhetőségét és tartalmát vizsgálja.

## Ismert hibák és korlátok

### Ismert hiba

A **`DeleteDataTest`** (TC-08) jelenleg szándékosan elbukik. A törlés sikeres (megjelenik a
„Category deleted.” üzenet), de a lista újratöltésekor a böngésző kb. 2 percig a
gyorsítótárból adja vissza a régi listát, amelyben a törölt kategória még szerepel. Az ok:
az oldal API-ja `Cache-Control: max-age=120` fejlécet küld a kategórialistára. Ez az
alkalmazás hibája, nem a teszté, ezért az ellenőrzés nincs megkerülve.

### Zárolt vásárlói fiók

A tesztek a demóoldal **nyilvános, közös** tesztfiókjait használják, amelyekbe bárki
beléphet. Ha valaki más többször rossz jelszóval próbál belépni a vásárlói fiókba
(`customer@practicesoftwaretesting.com`), az oldal **zárolja a fiókot**:

```
Account locked, too many failed attempts. Please contact the administrator.
```

Ilyenkor a **`LoginTest` TC-02.2 (vásárlói bejelentkezés)** elbukik – a teszt 10 másodperc
után időtúllépéssel áll le, mert az oldal nem lép tovább a login oldalról –, és ugyanezért
a **`DownloadDataTest` (TC-05)** is bukhat, mert az is ezt a fiókot használja. Ez nem a
tesztek hibája: a zárolást kívülről, más felhasználók hibás bejelentkezései okozzák.

**Teendő:** a zárolás a demóoldal adatbázisának rendszeres visszaállításával feloldódik.
Hogy feloldódott-e, egyetlen paranccsal ellenőrizhető:

```bash
./mvnw test -Dtest=LoginTest
```

Ha nem akarunk várni, a `utils/TestConfig` osztályban egy sorral át lehet állni a demóoldal
egy másik beépített vásárlói fiókjára (pl. `customer2@practicesoftwaretesting.com`).
A saját negatív teszt (TC-02.3) szándékosan nem létező fiókot használ, így ezt a zárolást
nem a tesztcsomag okozza.

### Közös, éles demóoldal

- Az oldal adatbázisa időnként visszaáll, és mások is használják; a tesztek ezért minden
  futáskor saját, egyedi adatokat hoznak létre.
- A **számlaletöltés (TC-05)** a demóoldal állapotától függően bukhat: a PDF-et a szerver
  a háttérben készíti, és nem minden számlához készül el. A teszt az első 5 számlát
  sorban megnézi, és az első letölthetőt tölti le; ha egyikhez sem készült el a PDF,
  a teszt később újrafuttatva várhatóan sikeres lesz.

## Futtatás helyben

### Előfeltételek

- JDK 21 (a `JAVA_HOME` környezeti változó erre mutasson, vagy a `java` legyen a PATH-on)
- Maven telepítése **nem szükséges**: a projekt Maven Wrappert tartalmaz, amely első
  futáskor automatikusan letölti a megfelelő Maven verziót (3.10.0)
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

A projekt mappájában, a Maven Wrapperrel:

| Rendszer | Parancs |
|---|---|
| Windows (PowerShell, cmd) | `.\mvnw.cmd clean test` |
| Linux, macOS, Git Bash | `./mvnw clean test` |

Egyetlen tesztosztály:

```bash
./mvnw test -Dtest=LoginTest
```

A README további parancsai a `./mvnw` formát használják; Windowson helyette `.\mvnw.cmd`
írandó. Ha a gépen telepítve van a Maven, a parancsok `mvn`-nel is működnek.

### Bemutató mód (lassítás)

A tesztek alapértelmezetten teljes sebességgel futnak. Bemutatóhoz bekapcsolható egy
lassítás, ilyenkor minden kattintás és gépelés előtt a megadott ideig (ezredmásodpercben)
várnak, hogy a futás szemmel követhető legyen:

```bash
./mvnw test -Dslowmo=800
```

Eclipse-ből futtatva a lassítás a run configuration *Arguments → VM arguments* mezőjében
adható meg (`-Dslowmo=800`). A lassítás csak a szemnek szól: az elemek megjelenésére
a tesztek mindenhol explicit várakozással (`WebDriverWait`) várnak.

## Allure riport

A tesztek futás közben a `target/allure-results` mappába írják az eredményeket.
Ebből a riport:

```bash
./mvnw allure:serve
```

Ez elkészíti a riportot és megnyitja a böngészőben. Csak a fájlok előállításához
(böngésző nélkül):

```bash
./mvnw allure:report
```

Ilyenkor a riport a `target/site/allure-maven-plugin/index.html` fájlba kerül. Fájlból
(dupla kattintással) megnyitva a böngésző nem tölti be az adatokat; a már elkészült
riport a projektben lévő Allure-eszközzel nyitható meg:

```bash
.allure\allure-2.20.1\bin\allure.bat open target\site\allure-maven-plugin
```

Ha a riportot a bukott tesztekkel együtt szeretnéd egy lépésben elkészíteni, add hozzá a
`-Dmaven.test.failure.ignore=true` kapcsolót, különben a Maven a bukott teszt után megáll,
és a riport nem készül el. A teljes folyamat egy paranccsal:

```bash
./mvnw clean test allure:report -Dmaven.test.failure.ignore=true
```

### Vezetői nézet az Allure riportban

A riport *Overview* oldala vezetői összefoglalóként is használható (sikerességi arány,
futási idő, a tesztesetek TC-azonosítóval). Ezt két fájl egészíti ki a `src/test/allure`
mappában, amelyeket a `pom.xml` minden Maven-futás elején automatikusan bemásol a
`target/allure-results` mappába:

- **`environment.properties`** – az *Environment* blokk: a tesztelt rendszer és URL,
  a böngésző, a Java és a használt eszközök.
- **`categories.json`** – a *Categories* fül: a bukásokat érthető csoportokba sorolja,
  így első ránézésre látszik, hogy egy bukás termékhiba vagy környezeti ok:
  - *Ismert alkalmazáshiba (H-01)* – a törölt kategória a listában marad
  - *Környezeti hiba (Cloudflare)* – a tesztelt oldal blokkolja a böngészőt
  - *Demóoldal állapota* – egyik számlához sem készült el a PDF
  - *Egyéb sikertelen ellenőrzés* és *Tesztfutási hiba* – minden más, vizsgálandó bukás
    (például a zárolt vásárlói fiók miatti időtúllépés is ide kerül)

A két fájl csak Mavennel futtatva kerül a riportba (parancssor, *Run As → Maven build*
vagy GitHub Actions); Eclipse-ből *Run As → JUnit Test*-tel futtatva nem.

## Tesztjelentések

A tesztek és a technikai riport (Surefire, Allure) automatikusan készül minden futáskor;
a vezetői jelentés ezekből az eredményekből, kézi értékeléssel készült egy adott futásról.

| Jelentés | Hogyan készül | Hol található |
|---|---|---|
| Surefire riport | automatikusan, minden futáskor | `target/surefire-reports/` |
| Allure riport | automatikusan, egy paranccsal; környezeti adatokkal és a bukások kategorizálásával (lásd fent) | `target/allure-results/`, `target/site/allure-maven-plugin/` |
| Vezetői tesztjelentés | kézzel, egy adott futás eredményei alapján | [`docs/Vezetoi_tesztjelentes.md`](docs/Vezetoi_tesztjelentes.md), [PDF](docs/Vezetoi_tesztjelentes.pdf) |

## GitHub Actions

A tesztek GitHub Actions-ben is futnak. A workflow: [`.github/workflows/ui-tests.yml`](.github/workflows/ui-tests.yml)

**Mikor fut:** minden push és pull request után, valamint kézzel is indítható
(*Actions* fül → *UI tesztek* → *Run workflow*).

**Mit csinál:**

1. Java 21-et telepít, és ellenőrzi, hogy a GitHub gépe eléri-e a tesztelt oldalt
   (HTTP státusz, oldalcím; lásd lent: [Cloudflare-védelem](#cloudflare-védelem-github-actions)).
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
részből tölthető le. Kicsomagolás után az `allure open <mappa>` paranccsal vagy egy helyi
webszerverrel érdemes megnyitni, mert fájlból megnyitva a böngésző nem tölti be az adatokat.

Ha bármelyik teszt elbukik, a futás piros lesz, de a riportok ettől még elkészülnek.

### Cloudflare-védelem (GitHub Actions)

**GitHub Actions-ben jelenleg minden teszt elbukik, de nem a tesztek hibája miatt.**

A tesztelt oldal (practicesoftwaretesting.com) Cloudflare védelem mögött van, amely a
GitHub Actions adatközponti gépeiről érkező kéréseket automatizált forgalomnak tekinti.
Ezekre a valódi oldal helyett egy botellenőrző oldalt ad vissza, így a böngészőben meg sem
jelenik az alkalmazás, és a tesztek már az első lépésnél elbuknak.

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

A Cloudflare ellenőrzését a tesztek szándékosan nem kerülik meg. Zöld CI-futáshoz egy
saját gépen futó (self-hosted) GitHub runner lenne a megoldás, ahonnan az oldal elérhető.

## Felhasznált tesztadatok

- **Admin és vásárlói fiók:** a demóoldal nyilvános, beépített tesztfiókjai. Az e-mail
  címek és jelszavak – a tesztelt oldal címével együtt – a `utils/TestConfig` osztályban
  vannak, egy helyen. A fiókok közösek, ezért mások hibás bejelentkezései zárolhatják
  őket (lásd: [Zárolt vásárlói fiók](#zárolt-vásárlói-fiók)).
- **Egyedi adatok:** a regisztráció (TC-01.1), a sikertelen bejelentkezés (TC-02.3) és a
  kategóriák (TC-06–TC-09) minden futáskor időbélyeget tartalmazó, egyedi e-mail címet,
  illetve slugot használnak.
- **`categories.csv`:** a `RepeatedCategoryEntryTest` bemenete, pontosvesszővel elválasztva,
  fejléc sorral (`name;parent_category;slug`). A teszt minden slug végére futásonként
  egyedi utótagot tesz.
