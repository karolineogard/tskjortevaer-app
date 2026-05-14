# Modellering for T-skjortevær

## Arkitekturskisse
```mermaid
---
config:
  layout: fixed
---
flowchart TB
 subgraph UI_Layer["UI / Presentasjonslag"]
    direction TB
        HS["HomeScreen"]
        MS["MapScreen"]
        CS["ClothesScreen"]
        LFS["LocationForecastScreen"]
        HVM["HomeViewModel"]
        MVM["MapViewModel"]
        CVM["ClothesViewModel"]
        LFVM["LocationForecastViewModel"]
  end
 subgraph Data_Layer["Datalaget"]
    direction TB
        LFR["LocationForecastRepository"]
        WR["WeatherRepository"]
        LR["LocationRepository"]
        LFDS["LocationForecastDataSource"]
        MADS["MetAlertsDatasource"]
        MLSDK["MapLibre SDK / Kart-motor"]
  end
 subgraph External["Eksterne APIer"]
        MET(("Victoria WMS"))
        Alert(("MetAlerts"))
        Location(("LocationForecast"))
        GEO(("Nominatim"))
  end
    HS --- HVM
    MS --- MVM
    CS --- CVM
    LFS --- LFVM
    HVM --> LFR & LR & WR
    MVM --> WR
    WR --> MLSDK & MADS
    CVM --> LFR
    LFVM --> LFR
    LFR --> LFDS
    LFDS --- Location
    MADS --- Alert
    MLSDK --- MET & Alert
    LR --- GEO

    style HS fill:#d1f2eb,stroke:#333
    style MS fill:#d6eaf8,stroke:#333
    style CS fill:#fdf2e9,stroke:#333
    style LFS fill:#f9ebea,stroke:#333
    style HVM fill:#d1f2eb,stroke:#333
    style MVM fill:#d6eaf8,stroke:#333
    style CVM fill:#fdf2e9,stroke:#333
    style LFVM fill:#f9ebea,stroke:#333
    style MLSDK fill:#3498db,color:#fff
    style MET fill:#fff,stroke-dasharray: 5 5
    style Alert fill:#fff,stroke-dasharray: 5 5
    style Location fill:#fff,stroke-dasharray: 5 5
    style GEO fill:#fff,stroke-dasharray: 5 5
```

## Tekstlige use case
### Klesanbefaling
Primæraktør: Bruker
Sekundæraktør: LocationForecast, MetAlerts
Prebetingelser: Bruker har installert appen T-skjortevær på sin enhet
Postbetingelser: Ingen

##### Hovedflyt
1. Bruker åpner appen 
2. Appen ber om tilgang til lokasjon
3. Bruker tillatter lokasjonstilgang
4. Appen henter brukerens posisjon
5. Appen viser været, mulige farevarsler og klesanbefaling basert på brukerens posisjon

##### Alternativ flyt punkt 3
3.1 Bruker tillater ikke lokasjonstilgang
3.2 Appen går videre med default-lokasjon (Oslo)

### Søk på sted
Primæraktør: Bruker
Sekundæraktør:
Prebetingelser: Bruker har installert appen T-skjortevær på sin enhet
Postbetingelser: Ingen

##### Hovedflyt


##### Alternativ flyt punkt 


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
        -WeatherRepository weatherRepository
        -LocationRepository locationRepository
        +MutableStateFlow~UiState~ uiState
        +loadData()
    }

    class UiState {
        <<sealed>>
    }

    class Success {
        +AppLocation location
        +ForecastHourDetails forecast
        +String place
        +List~MetAlert~ alerts
        +ClothesRecommendation recommendation
    }

    %% Business Logic
    class ClothesRecommendationEngine {
        <<utility>>
        +recommend(forecasts, settings, offset) ClothesRecommendation
    }

    %% Repositories / Data Sources
    class DeviceLocationDataSource {
        +getCurrentLocation() AppLocation
    }

    class LocationForecastRepository {
        -LocationForecastDataSource dataSource
        +getForecastNow(lat, lon) ForecastHourDetails
    }

    class WeatherRepository {
        -MetAlertsDatasource alertDataSource
        +getAlertsByLocation(lat, lon) List~MetAlert~
    }

    class LocationRepository {
        +getPlaceName(lat, lon) String
    }

    %% Data Models
    class AppLocation {
        +Double lat
        +Double lon
    }

    class ForecastHourDetails {
        +Double airTemperature
        +Double windSpeed
        +String symbolCode
        +Double precipitationAmount
    }

    %% Relationships
    HomeScreen --> HomeViewModel : observerer
    HomeViewModel ..> UiState : oppdaterer
    UiState <|-- Success
    
    HomeViewModel --> DeviceLocationDataSource : bruker
    HomeViewModel --> LocationForecastRepository : bruker
    HomeViewModel --> WeatherRepository : bruker
    HomeViewModel --> LocationRepository : bruker
    HomeViewModel ..> ClothesRecommendationEngine : beregner via

    LocationForecastRepository --> AppLocation : krever
    WeatherRepository --> AppLocation : krever
    
    Success --> AppLocation
    Success --> ForecastHourDetails

```

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


## Use-case diagram

<img width="1172" height="637" alt="image" src="https://github.uio.no/user-attachments/assets/7e0cf22a-5bf6-4b1c-8399-eeb85b0f08d9" />

