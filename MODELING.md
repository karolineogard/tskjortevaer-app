# Modellering for T-skjortevær

## User stories
Vi har samlet noen user stories som konkretiserer noen av de sentrale funksjonelle kraven i appen. 
1. Som student vil jeg vite hva jeg burde ha på meg når jeg pendler til universitetet så jeg føler meg komfortabel
2. Som danser vil jeg kunne tilpasse klesanbefalingen min etter aktivitetsnivå så jeg ikke blir for varm
3. Som student vil jeg kunne tilpasse reisetidene mine så jeg får en klesanbefaling tilpassset mine behov
4. Som en frysepinne vil jeg kunne tilpasse innstillenger så jeg får klesanbefalinger tilpasset hvor kald jeg føler meg, så jeg slipper å fryse
   
## Tekstlige use case
### Klesanbefaling
Primæraktør: Bruker \
Sekundæraktør: LocationForecast, MetAlerts \
Prebetingelser: Bruker har installert appen T-skjortevær på sin enhet \
Postbetingelser: Bruker har fått vist en klesanbefaling basert på værvarselet for sin posisjon

##### Hovedflyt
1. Bruker åpner appen 
2. Appen ber om tilgang til lokasjon
3. Bruker tillater lokasjonstilgang
4. Appen henter brukerens posisjon
5. Appen viser været, mulige farevarsler og klesanbefaling basert på brukerens posisjon

##### Alternativ flyt punkt 3
3.1 Bruker tillater ikke lokasjonstilgang \
3.2 Appen går videre med default-lokasjon (Oslo) 

##### Alternativ flyt punkt 5
5.1 Appen får ikke hentet data fra API \
5.2 Appen viser feilmelding om manglende internettforbindelse

### Søk på sted
Primæraktør: Bruker \
Sekundæraktør: Nominatim, Locationforecast \
Prebetingelser: Bruker har installert appen T-skjortevær på sin enhet \
Postbetingelser: Bruker har fått vist værmelding for stedet den søkte opp

##### Hovedflyt
1. Bruker åpner appen 
2. Bruker trykker i søkefeltet på hjemskjermen 
3. Bruker taster inn "oslo" 
4. Appen viser forslag basert på søketeksten 
5. Bruker trykker på et av forslagene 
6. Appen henter værmelding for det valgte stedet fra LocationForecast
7. Appen viser værmelding for stedet

##### Alternativ flyt – punkt 4
4.1 Bruker trykker søk uten å velge et forslag \
4.2 Appen slår opp koordinater for søketeksten direkte \
4.3 Appen fortsetter til punkt 6

##### Alternativ flyt – punkt 4 (ingen treff)
4.1 Nominatim finner ingen koordinater for søketeksten \
4.2 Appen viser ingen værmelding \
4.3 Bruker kan endre søketeksten og prøve på nytt

##### Alternativ flyt – punkt 6
6.1 Appen får ikke hentet data (ingen internettforbindelse) \
6.2 Appen viser feilmelding om manglende internettforbindelse


## Aktivitetsdiagram
```mermaid
flowchart TD
    Start((Start))
    Åpne([Bruker åpner appen])
    Lokasjon([Appen spør bruker om tilgang til lokasjon])
    Standard([Bruker standard-lokasjon])
    Tilgang{Tillat lokasjonstilgang?}
    Vær([Henter værmelding for lokasjon])
    Klær([Viser klesanbefaling og værmelding for lokasjon])
    Internett{Internett-tilgang?}
    Feilmelding([Viser feilmelding])
    Søk{Søker på lokasjon?}
    Skriv([Bruker skriver inn stedsnavn])
    Forslag{Velger forslag?}
    Koordinater([Appen slår opp koordinater])
    Treff{Treff funnet?}
    Endre([Bruker endrer søketekst])
    Værmelding([Henter værmelding for sted])
    Værmelding2([Viser værmelding for sted])
    Internett2{Internett-tilgang?}
    Feilmelding2([Viser feilmelding])
    Slutt(((Slutt)))

    Start --> Åpne
    Åpne --> Lokasjon
    Lokasjon --> Tilgang
    Tilgang --Tillater tilgang--> Vær
    Tilgang --Tillater ikke tilgang --> Standard
    Standard --> Vær
    Vær --> Internett
    Internett --Ingen internett-tilgang--> Feilmelding
    Internett --Har internett-tilgang--> Klær
    Feilmelding --> Slutt
    Søk --Nei--> Slutt
    Søk --Ja--> Skriv
    Skriv --> Forslag
    Forslag --Nei--> Koordinater
    Forslag --Ja--> Værmelding
    Koordinater --> Treff
    Treff --Ja--> Værmelding
    Treff --Nei--> Endre
    Værmelding --> Internett2
    Internett2 --Ja--> Værmelding2
    Internett2 --Nei--> Feilmelding2
    Feilmelding2 --> Slutt
    Værmelding2 --> Slutt
    Endre --> Skriv
    Klær --> Søk
```
Aktivitetsdiagrammet er basert på de tekstlige use casene over kombinert. 
## Klassediagram
```mermaid
classDiagram
    direction TB

    %% View / UI
    class HomeScreen {
        <<Composable>>+HomeViewModel viewModel
    }

    %% ViewModel
    class HomeViewModel {
        -DeviceLocationDataSource deviceLocationDataSource
        -LocationForecastRepository locationForecastRepository
        -MetAlertsRepository alertsRepository 
        -LocationRepository locationRepository
        +StateFlow~UiState~ uiState
        +loadData()
    }

    class ClothesViewModel{
     -LocationForecastRepository repository
    -DataStore dataStore
    +Double currentLat
    +Double currentLon
    +StateFlow~UserSettings~ settings
    +StateFlow~ClothesRecommendation~ recommendation
    +StateFlow~Boolean~ isLoading
    +updateLocation(lat, lon)
    +updateSettings(depHour, depMinute, retHour, retMinute, isOutdoors, isPhysical, activityLevel)
    +loadRecommendation()
}


    %% Business Logic
    class ClothesRecommendationEngine {
        <<utility>>
        +recommend(forecasts, settings, offset) ClothesRecommendation
    }

    %% Repositories / Data Sources

class DataStore {
    <<persistence>>
}
    class DeviceLocationDataSource {
        +getCurrentLocation() AppLocation
    }

    class LocationForecastRepository {
        -LocationForecastDataSource dataSource
        +getForecastNow(lat, lon) ForecastHourDetails
    }

    class LocationRepository {
        +getPlaceName(lat, lon) String
    }

    class MetAlertsRepository {
        +getAlertsByLocation(lat, lon) List~MetAlert~
}

    %% Data Models
    class AppLocation {
        +Double lat
        +Double lon
    }

    class ForecastHourDetails {
        +Double temperature
        +Double windSpeed
        +String symbolCode
        +Double precipitationAmount
    }
class ClothesRecommendation {
    +Double effectiveTemp
    +Boolean wearHeavyJacket
    +Boolean wearLightJacket
    +Boolean wearSweater
    +Boolean wearTshirt
    +Boolean wearShorts
    +Boolean bringUmbrella
    +Boolean wearRainGear
}

class UserSettings {
    +Int departureHour
    +Int departureMinute
    +Int returnHour
    +Int returnMinute
    +Boolean isOutdoors
    +Boolean isPhysicallyActive
    +ActivityLevel activityLevel
}

    class UiState {
        <<sealed>>
    }

    class Success {
        +AppLocation location
        +ForecastHourDetails forecast
        +String place
        +List~MetAlert~ alerts
    }
UiState <|-- Loading
UiState <|-- Error

    %% Relationships
    HomeScreen --> HomeViewModel : observerer
    HomeViewModel ..> UiState : oppdaterer
    UiState <|-- Success
    
    HomeViewModel --> DeviceLocationDataSource : bruker
    HomeViewModel --> LocationForecastRepository : bruker
    HomeViewModel --> MetAlertsRepository : bruker
    HomeViewModel --> LocationRepository : bruker
    
    Success --> AppLocation
    Success --> ForecastHourDetails
    ClothesViewModel --> LocationForecastRepository : bruker
    ClothesViewModel --> DataStore : observerer
    ClothesViewModel ..> ClothesRecommendationEngine : beregner via
    ClothesRecommendationEngine ..> ClothesRecommendation : returnerer
    ClothesViewModel --> UserSettings
    ClothesViewModel --> ClothesRecommendation

```
Viser delene av kodebasen som er relevante for use case klesanbefaling
## sekvensdiagram

```mermaid
sequenceDiagram
    actor Bruker
    participant UI as App/UI
    participant VM as ViewModel    
    participant Loc as LocationService
    participant Repo as WeatherRepository
    participant API as MET API

    Bruker->>UI: Åpner appen
    UI->>Bruker: Ber om lokasjonstilgang
    
    alt Bruker tillater lokasjon
        Bruker->>UI: Godtar tilgang
        UI->>VM: Start henting (med tillatelse)
        VM->>Loc: Hent brukerens posisjon
        Loc-->>VM: Returnerer koordinater (lat, lon)
    else Bruker tillater ikke lokasjon
        Bruker->>UI: Avslår tilgang
        UI->>VM: Start henting (uten tillatelse)
        VM->>VM: Bruk default-lokasjon (Oslo)
    end

    VM->>Repo: Hent værdata(koordinater)
    Repo->>API: forespørsel (LocationForecast, MetAlerts)
    API-->>Repo: Værdata (JSON)
    Repo-->>VM: Prosessert værdata
    
    VM->>VM: Beregn klesanbefaling basert på vær
    VM-->>UI: Oppdater state (Vær + Klær)
    UI-->>Bruker: Viser vær og klesanbefaling
```


## Use Case Diagram
Her har vi laget et Use Case Diagram. Et Use Case Diagram viser målene til primæraktøren og hvordan sekundæraktører hjelper med å nå dette målet gjennom systemet. Dette diagrammet er laget til use caset  «klesanbefaling». 

<img width="1172" height="637" alt="image" src="https://github.uio.no/user-attachments/assets/7e0cf22a-5bf6-4b1c-8399-eeb85b0f08d9" />

