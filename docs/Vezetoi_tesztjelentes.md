# Vezetői tesztjelentés

**Projekt:** Practice Software Testing (Toolshop) – automatizált UI tesztek
**Tesztelt rendszer:** https://practicesoftwaretesting.com/ (Toolshop v5.0)
**Futtatás időpontja:** 2026. november 6., 17:30
**Készítette:** Csongár Szabolcs

---

## 1. Összefoglaló

A webáruház kulcsfontosságú funkcióit 12 automatizált tesztesettel ellenőriztük.
**12 tesztből 11 sikeres (92%).** A regisztráció, a be- és kijelentkezés (admin és vásárló), a termékböngészés,
a számlaletöltés, a kategóriák felvétele, módosítása és az adatkezelési nyilatkozat
megjelenítése hibátlanul működik.

**1 hibát találtunk:** a kategória törlése után az admin felület kb. 2 percig még a törölt
kategóriát is mutatja a listában. Maga a törlés megtörténik, de a felhasználó ezt nem látja,
ami félrevezető, és ismételt törlési kísérletekhez vezethet.

## 2. A tesztelés célja és hatóköre

**Cél:** a webáruház legfontosabb felhasználói és adminisztrátori folyamatainak ellenőrzése
a böngészőben, úgy, ahogy egy valódi felhasználó használná.

**Tesztelt területek:**

| Terület | Funkciók |
|---|---|
| Felhasználókezelés | regisztráció, bejelentkezés (admin és vásárló), kijelentkezés |
| Vásárlói funkciók | terméklista lapozása, számla PDF letöltése |
| Adminisztráció (kategóriák) | felvétel, módosítás, törlés, listázás, több kategória felvétele fájlból |
| Adatkezelés | az adatkezelési nyilatkozat (Privacy Policy) megnyitása és tartalma |

**Nem tesztelt területek:**

- **Adatkezelési nyilatkozat elfogadása** – az oldalon nincs elfogadandó nyilatkozat
  (pl. jelölőnégyzet a regisztrációnál), ezért csak a nyilatkozat elérhetősége és
  tartalma tesztelhető (TC-11).
- Fizetés, kosár, rendelésleadás – nem része a tesztelési hatókörnek.
- Teljesítmény-, terhelés- és biztonsági tesztelés – nem része a hatókörnek.

## 3. Tesztkörnyezet

| | |
|---|---|
| Böngésző | Google Chrome 156 (ablak nélküli / headless módban) |
| Operációs rendszer | Windows 11 |
| Tesztelt környezet | éles demóoldal (practicesoftwaretesting.com) |
| Eszközök | Java 21, Selenium WebDriver 4.51, JUnit 6.1, Allure riport |
| Teljes futási idő | kb. 1 perc 42 másodperc |

## 4. Eredmények

| TC | Teszteset | Eredmény | Idő |
|---|---|---|---|
| TC-01 | Sikeres regisztráció új felhasználóval | ✅ Sikeres | 11,4 s |
| TC-02 | Admin bejelentkezés | ✅ Sikeres | 5,1 s |
| TC-02.1 | Vásárlói bejelentkezés | ✅ Sikeres | 4,3 s |
| TC-03 | Kijelentkezés | ✅ Sikeres | 5,0 s |
| TC-04 | Terméklista lapozása (1–5. oldal) | ✅ Sikeres | 4,3 s |
| TC-05 | Számla PDF letöltése | ✅ Sikeres | 5,8 s |
| TC-06 | Kategória felvétele | ✅ Sikeres | 6,5 s |
| TC-07 | Kategória módosítása | ✅ Sikeres | 7,7 s |
| TC-08 | Kategória törlése | ❌ **Sikertelen** | 17,1 s |
| TC-09 | 4 kategória felvétele egymás után CSV fájlból | ✅ Sikeres | 12,2 s |
| TC-10 | Kategórialista megjelenítése | ✅ Sikeres | 4,9 s |
| TC-11 | Adatkezelési nyilatkozat megnyitása | ✅ Sikeres | 3,0 s |

**Összesítés:**

| Összes teszt | Sikeres | Sikertelen | Kihagyott | Sikerességi arány |
|---|---|---|---|---|
| 12 | 11 | 1 | 0 | **92%** |

## 5. Talált hibák

### H-01: Törölt kategória a törlés után is látszik a listában

| | |
|---|---|
| **Súlyosság** | Közepes |
| **Érintett funkció** | Admin → Categories → Delete |
| **Állapot** | Nyitott (az alkalmazás hibája) |

**Leírás:** a kategória törlésekor megjelenik a „Category deleted.” sikerüzenet, és a
kategória ténylegesen törlődik. A lista azonban ezután ugyanarról a címről töltődik újra,
és a böngésző kb. 2 percig a gyorsítótárból (cache) a régi listát mutatja, amelyben a törölt
kategória még szerepel.

**Hatás a felhasználóra:** az adminisztrátor azt látja, hogy a törlés nem sikerült, ezért
újra próbálkozhat, vagy hibát jelenthet. Adatvesztés nem történik.

## 6. Korlátok és kockázatok

- **Automatikus futtatás a GitHubon nem lehetséges.** A tesztelt oldal Cloudflare-védelme
  a GitHub Actions szervereiről érkező kéréseket blokkolja („Just a moment...” ellenőrző oldal,
  403-as válasz). A tesztek ezért csak helyi gépen futtathatók; ez a tesztelt oldal sajátossága,
  nem a tesztek hibája. Megoldás lehet egy saját gépen futó (self-hosted) GitHub runner.
- **Éles, közös demóoldalon futnak a tesztek.** Az oldalt mások is használják, és az adatbázisa
  időnként visszaáll. A tesztek ezért minden futáskor egyedi adatokat hoznak létre (egyedi
  e-mail cím, egyedi kategórianevek), így egymást és más felhasználókat nem zavarják.
- **A demóoldal tesztadatokat halmoz fel.** A felvett tesztkategóriák az oldalon maradnak,
  amíg az adatbázis vissza nem áll.
- **A számlaletöltés teszt a demóoldal állapotától függően bukhat.** A PDF-et a szerver
  a háttérben készíti el, és nem minden számlához készül el (pl. a demóoldal adatbázisának
  visszaállítása után). A teszt ezért az első 5 számlát sorban megnézi, és az első olyat
  tölti le, amelyikhez a PDF már elkészült. Ha egyiknél sem érhető el a PDF, a teszt
  elbukik; ilyenkor a hiba a demóoldal pillanatnyi állapotából adódik, nem a letöltés
  funkcióból, ezért a teszt később újrafuttatva várhatóan sikeres lesz.

## 7. Konklúzió és javaslatok

1. **A tesztelt funkciók 92%-a hibátlanul működik**, a kritikus folyamatok (regisztráció,
   belépés, vásárlói funkciók, adatfelvétel és -módosítás) rendben vannak.
2. **A rendszer használható, a H-01 hiba nem akadályozza a működést.** A törölt kategória
   kb. 2 percig még látszik a listában; ez félrevezető, de adatvesztést nem okoz.

---

*Melléklet: részletes, grafikus eredmények az Allure riportban (`mvn allure:serve`).*
