# Vezetői tesztjelentés

**Projekt:** Practice Software Testing (Toolshop) – automatizált UI tesztek
**Tesztelt rendszer:** https://practicesoftwaretesting.com/ (Toolshop v5.0)
**Készítette:** Csongár Szabolcs

---

## 1. Összefoglaló

A webáruház 10 kulcsfontosságú funkcióját automatizált tesztekkel ellenőriztük.
**10 tesztből 9 sikeres (90%).** A regisztráció, a be- és kijelentkezés, a termékböngészés,
a számlaletöltés és a kategóriák felvétele, módosítása hibátlanul működik.

**1 hibát találtunk:** a kategória törlése után az admin felület kb. 2 percig még a törölt
kategóriát is mutatja a listában. Maga a törlés megtörténik, de a felhasználó ezt nem látja,
ami félrevezető, és ismételt törlési kísérletekhez vezethet.

## 2. A tesztelés célja és hatóköre

**Cél:** a webáruház legfontosabb felhasználói és adminisztrátori folyamatainak ellenőrzése
a böngészőben, úgy, ahogy egy valódi felhasználó használná.

**Tesztelt területek:**

| Terület | Funkciók |
|---|---|
| Felhasználókezelés | regisztráció, bejelentkezés, kijelentkezés |
| Vásárlói funkciók | terméklista lapozása, számla PDF letöltése |
| Adminisztráció (kategóriák) | felvétel, módosítás, törlés, listázás, több kategória felvétele fájlból |

**Nem tesztelt területek:**

- **Adatkezelési nyilatkozat használata** – a tesztelt oldalon nincs ilyen funkció
  (nincs megtekinthető vagy elfogadható adatkezelési nyilatkozat), ezért nem tesztelhető.
- Fizetés, kosár, rendelésleadás – nem része a tesztelési hatókörnek.
- Teljesítmény-, terhelés- és biztonsági tesztelés – nem része a hatókörnek.

## 3. Tesztkörnyezet

| | |
|---|---|
| Böngésző | Google Chrome 156 (ablak nélküli / headless módban) |
| Operációs rendszer | Windows 11 |
| Tesztelt környezet | éles demóoldal (practicesoftwaretesting.com) |
| Eszközök | Java 21, Selenium WebDriver 4.51, JUnit 6.1, Allure riport |
| Teljes futási idő | kb. 1 perc 47 másodperc |

## 4. Eredmények

| # | Teszteset | Eredmény | Idő |
|---|---|---|---|
| 1 | Sikeres regisztráció új felhasználóval | ✅ Sikeres | 13,7 s |
| 2 | Admin bejelentkezés | ✅ Sikeres | 5,3 s |
| 3 | Kijelentkezés | ✅ Sikeres | 7,5 s |
| 4 | Terméklista lapozása (1–5. oldal) | ✅ Sikeres | 4,9 s |
| 5 | Számla PDF letöltése | ✅ Sikeres | 7,3 s |
| 6 | Kategória felvétele | ✅ Sikeres | 6,8 s |
| 7 | Kategória módosítása | ✅ Sikeres | 10,3 s |
| 8 | Kategória törlése | ❌ **Sikertelen** | 18,7 s |
| 9 | 4 kategória felvétele egymás után CSV fájlból | ✅ Sikeres | 11,6 s |
| 10 | Kategórialista megjelenítése | ✅ Sikeres | 7,8 s |

**Összesítés:**

| Összes teszt | Sikeres | Sikertelen | Kihagyott | Sikerességi arány |
|---|---|---|---|---|
| 10 | 9 | 1 | 0 | **90%** |

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

## 7. Konklúzió és javaslatok

1. **A tesztelt funkciók 90%-a hibátlanul működik**, a kritikus folyamatok (regisztráció,
   belépés, vásárlói funkciók, adatfelvétel és -módosítás) rendben vannak.
2. **A rendszer használható, a H-01 hiba nem akadályozza a működést.** A törölt kategória
   kb. 2 percig még látszik a listában; ez félrevezető, de adatvesztést nem okoz.

---

*Melléklet: részletes, grafikus eredmények az Allure riportban (`mvn allure:serve`).*
