# T-skjortevær 🌤️

> Gruppeprosjekt utviklet ved Universitetet i Oslo, våren 2026.

Dette repositoryet er en personlig portfolio-kopi av et prosjekt utviklet i gruppe som del av IN2000 ved UiO.

## Mitt bidrag

Jeg bidro blant annet med:
- Implementering av navigasjon mellom appens ulike skjermer og funksjoner
- Utvikling av frontend og brukergrensesnitt (UI)
- Implementering av UI-komponenter og funksjonalitet med Jetpack Compose

## Teknologi

- Kotlin
- Jetpack Compose / XML
- Android Studio





**Team 32** — IN2000, Institutt for informatikk, UiO

> En Android-app som viser værkart, farevarsler og klesanbefalinger basert på værvarsel for din lokasjon.

---

## Teammedlemmer

| Navn |
|------|
| Ingrid |
| Karoline |
| Eirik |
| Isak |
| Didac |
| Lawmi |

---

## Funksjonalitet

- **Værkart** - interaktivt kart med kartlag for temperatur, nedbør og vind hentet fra MET sitt Victoria WMS-grensesnitt. Brukeren kan styre hvilket tidspunkt som vises, og søke på lokasjon. Kartet vil da flyttes til stedet som ble søkt på. 
- **Farevarsler** - aktive farevarsler vises som polygoner på kartet via MetAlerts-APIet.
- **Værvarsel** - trykk på et sted i kartet eller søk etter stedsnavn fra hjemskjermen for å se værvarsel for de neste dagene.
- **Klesanbefaling** - anbefaler klær basert på værvarsel for enhetens lokasjon og brukerens preferanser (avgangstid, aktivitetsnivå, temperaturpreferanse).
- **Innstillinger** - lagre standardtider og temperaturpreferanse mellom sesjonene.

---

## Kjøre appen

### Krav

- **Android Studio** Ladybug (2024.2) eller nyere
- **JDK 17** (følger med Android Studio)
- Internettforbindelse

### Steg

1. Klon repoet:
   ```bash
   git clone https://github.uio.no/IN2000-V26/team-32.git
   cd team-32
   ```

2. Åpne prosjektet i Android Studio (`File → Open`).

3. Vent til Gradle synkroniserer (første gang kan ta noen minutter).

4. [Koble til en enhet](https://developer.android.com/studio/run/device) eller start en emulator (API 24+). 

5. Trykk **Run ▶** eller bruk `Shift+F10`.

### Avhengigheter

- **Lokasjon** - Appen ber om tillatelse til grov lokasjon (`ACCESS_COARSE_LOCATION`) for å vise varsel og klesanbefaling for din posisjon. Dersom tillatelse avslås, faller appen tilbake til Oslo som standardlokasjon.
- **Internett** - Alle API-kall krever aktiv internettforbindelse. Appen viser en banner og håndterer feil dersom nettet er nede.

---

## API-nivå

| Parameter | Verdi |
|---|---|
| `minSdk` | **24** (Android 7.0 Nougat) |
| `targetSdk` | **35** (Android 15) |
| `compileSdk` | 36 |

**Begrunnelse for `minSdk = 24`:** API 24 dekker nesten alle aktive Android-enheter og gir tilgang til `java.time`-biblioteket via core library desugaring, noe vi bruker til å parse tid i `Format.kt` og `MapViewModel`. Å sette minstenivået lavere (f.eks. 21) ville krevd flere workarounds, som med tanke på prosjektets skop ikke gir mening å bruke tid på.

---

## Biblioteker

Disse bibliotekene brukes i appen. Biblioteker som ikke er vist i kurset, er forklart litt nærmere.

### Vist i kurset

| Bibliotek | Bruk |
|---|---|
| **Jetpack Compose** | Hele brukergrensesnittet |
| **Ktor (CIO + ContentNegotiation)** | HTTP-klienten for alle API-kall |
| **kotlinx.serialization** | JSON-deserialisering av API-responser |
| **Navigation Compose** | Navigasjon mellom skjermer |
| **ViewModel + StateFlow** | MVVM-arkitektur og tilstandshåndtering |

### Ikke vist i kurset
For alle biblioteker ikke vist i emnet etterstrebet vi å finne dem som var gratis, gjerne open source og hvor man ikke trengte å registrere seg for API-nøkler og lignende. Det har i ganske stor grad påvirket valgene vi har gjort. 

| Bibliotek | Versjon | Forklaring                                                                                                                                                                                                           |
|---|---------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **MapLibre Android** | 12.2.3   | Open source-kartklient som støtter WMS-kartlag. Vi bruker den til å vise Victoria-kartlagene og farevarsler som polygoner. Et alternativ hadde vært Google Maps, men MapLibre er gratis og uten API-nøkkel.          |
| **Hilt (Dagger)** | 2.56.1    | Dependency injection-rammeverk fra Google. Forenkler oppretting og deling av avhengigheter (repository, HTTP-klient, DataStore) mellom ViewModels uten å måtte sende dem manuelt gjennom konstruktørkjedene.         |
| **Coil 3** | 3.4.0    | Asynkron bildelasting for Compose. Brukes til å laste MET sitt SVG-baserte værsymbolbibliotek fra GitHub. Coil 3 støtter SVG via en egen dekoder (`coil-svg`) og bruker OkHttp for nettverkskall.                    |
| **Play Services Location** | -       | Googles `FusedLocationProviderClient` kombinerer GPS, mobilnett og Wi-Fi for å gi rask og batterivennlig posisjonering. Vi bruker `PRIORITY_BALANCED_POWER_ACCURACY` som er raskere enn ren GPS og nøyaktig nok for værvarsel. |
| **Core Library Desugaring** | -       | Gjør `java.time`-klasser tilgjengelige på enheter under API 26 (Android 8). Nødvendig siden vi bruker `Instant`, `ZoneOffset`, og `DateTimeFormatter` for tidsparsing og UTC-konvertering.                           |
| **MockK** | -       | Kotlin-native mock-bibliotek brukt i enhetstester. Støtter `suspend`-funksjoner og coroutines, i motsetning til Mockito som krever ekstra workarounds for Kotlin.                                                    |
| **JUnit 5 (Jupiter)** | -       | Testrammeverk med bedre støtte for parameteriserte tester og lesbare `@DisplayName`-annotasjoner enn JUnit 4.                                                                                                        |
| **kotlinx-coroutines-test** | 1.7.3       | Testverktøy for coroutines: `runTest`, `advanceUntilIdle`, `StandardTestDispatcher`. Lar oss teste asynkron logikk deterministisk.                                                                                   |
| **DataStore Preferences** | -       | Lagring av brukerinnstillinger mellom sesjonene                                                                                                                                                                      |

---

## API-er som brukes

| API | Endepunkt | Forklaring                                                                     |
|---|---|--------------------------------------------------------------------------------|
| LocationForecast 2.0 | `in2000.api.met.no/weatherapi/locationforecast/2.0/compact` | Brukes for å hente punktdata, både for brukerlokasjon og basert på søk.        |
| MetAlerts 2.0 | `in2000.api.met.no/weatherapi/metalerts/2.0/current.json` | Brukes for å hente farevarsler, både på hjemskjerm og som polygoner på kartet. |
| Victoria WMS | `public-victoria.met.no/wms` | Brukes for å hente kartlag i henhold til de funksjonelle kravene.              |
| Nominatim (OpenStreetMap) | `nominatim.openstreetmap.org` | Brukes for geocoding og søkefunksjonalitet.                                    |

MET sine API-er brukes via IFI sin proxyserver (`in2000.api.met.no`) i tråd med kurskravet. Unntaket er victoria, hvor det ikke er nødvendig å bruke proxyserver.

---

## Kilder til bilder
Værikonene er hentet fra MET sin github: https://github.com/metno
 
Andre ikoner brukt i appen for f.eks. navigasjon og handling, er hovedsakelig hentet fra Material 3 Design Kit sitt bibliotek samt Figma sitt "Simple Design Systems"-bibliotek innebygd i Figma. Klesikonene ble hentet fra et gratis bibliotek hentet fra Figma Community. Enkelte illustrasjonsikoner, f.eks. for "viking" er hentet fra diverse bibliotek gjennom den innebygde søkefunksjonen i Figma. Appikonene er tegnet selv.

Lenke til klesikoner: https://www.figma.com/design/ZboyOHzK2l8UFFlUQZYsVy/Clothes-Icon-Pack-%7C-1024-Free-Icons--Community-?node-id=0-1&p=f&t=CO3v92vkBkcgflhx-0


