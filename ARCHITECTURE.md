# Oversikt over arkitekturen i T-skjortevær
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
        SVM["SearchViewModel"]

        
  end
 subgraph Data_Layer["Datalaget"]
    direction TB
        LFR["LocationForecastRepository"]
        WR["WeatherRepository"]
        LR["LocationRepository"]
        LFDS["LocationForecastDataSource"]
        MADS["MetAlertsDatasource"]
        MLSDK["MapLibre SDK / Kart-motor"]
        DLDS["DeviceLocationDataSource"]
        AR["AlertsRepository"]
        LDS["LocationDataSource"]
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
    LR --> LDS
    LDS --- GEO
    MS --- SVM
    HS --- SVM
    SVM --> LFR
    HVM --> DLDS
    AR --> MADS
    HVM --> AR
    MVM --> AR
    

    style HS fill:#d1f2eb,stroke:#333
    style MS fill:#d6eaf8,stroke:#333
    style CS fill:#fdf2e9,stroke:#333
    style LFS fill:#f9ebea,stroke:#333
    style HVM fill:#d1f2eb,stroke:#333
    style MVM fill:#d6eaf8,stroke:#333
    style CVM fill:#fdf2e9,stroke:#333
    style LFVM fill:#f9ebea,stroke:#333
    style SVM fill:#F6E215,stroke:#333
    style MLSDK fill:#3498db,color:#fff
    style MET fill:#fff,stroke-dasharray: 5 5
    style Alert fill:#fff,stroke-dasharray: 5 5
    style Location fill:#fff,stroke-dasharray: 5 5
    style GEO fill:#fff,stroke-dasharray: 5 5
```

## Mappestruktur
```
app/src/main/java/no/uio/ifi/in2000/ieulrich/team32/
│
├── MainActivity.kt               <- Inngangspunkt, håndterer lokasjonstillatelse
├── MyApp.kt                      <- Hilt application-klasse
│
├── data/                         <- Datalaget — all kommunikasjon med API og enheten
│   ├── client/
│   │   └── NetworkMonitor.kt     <- Overvåker nettverkstilkobling (StateFlow<Boolean>)
│   ├── geocoding/                <- Stedsnavnoppslag via Nominatim
│   │   ├── dto/
│   │   │   ├── NominatimAddress.kt
│   │   │   ├── NominatimResponse.kt
│   │   │   └── NominatimSearchResult.kt
│   │   ├── LocationDatasource.kt
│   │   └── LocationRepository.kt
│   ├── location/                 <- GPS-posisjon fra enheten
│   │   └── DeviceLocationDataSource.kt
│   ├── locationForecast/         <- LocationForecast 2.0 (MET via IFI-proxy)
│   │   ├── dto/                  <- Rå API-responsobjekter
│   │   │   ├── Data.kt
│   │   │   ├── Geometry.kt
│   │   │   ├── Instant.kt
│   │   │   ├── InstantDetails.kt
│   │   │   ├── LocationForecastResponse.kt
│   │   │   ├── Meta.kt
│   │   │   ├── NextHours.kt
│   │   │   ├── PrecipitationDetails.kt
│   │   │   ├── Properties.kt
│   │   │   ├── Summary.kt
│   │   │   ├── TimeSeries.kt
│   │   │   └── Units.kt
│   │   ├── mapper/
│   │   │   └── ForecastMapper.kt          <- DTO til domenemodell
│   │   ├── LocationForecastDataSource.kt  <- HTTP-kall + If-Modified-Since caching
│   │   └── LocationForecastRepository.kt
│   ├── metAlert/                          <- MetAlerts (MET via IFI-proxy)
│   │   ├── MetAlertsDatasource.kt
│   │   └── MetAlertsRepository.kt
│   └── weather/                  <- Victoria WMS-kartlag
│       └── WeatherRepository.kt  <- WMS-URL-bygging
│
├── di/                           <- Hilt dependency injection-moduler
│   ├── NetworkModule.kt          <- HttpClient, FusedLocationProviderClient, DataStore
│   └── RepositoryModule.kt       <- Binder interface til implementasjon
│
├── model/                        <- Domeneobjekter som brukes på tvers av lag
│   ├── clothes/
│   │   ├── ActivityLevel.kt
│   │   └── ClothesRecommendationEngine.kt
│   ├── location/
│   │   └── AppLocation.kt
│   ├── locationForecast/
│   │   └── ForecastHourDetails.kt
│   ├── metAlerts/
│   │   ├── MetAlert.kt
│   │   └── MetAlertExtensions.kt
│   └── weather/
│       └── WeatherLayer.kt
│
├── ui/                           <- UI-laget (Jetpack Compose)
│   ├── components/               <- Gjenbrukbare Compose-komponenter
│   │   ├── ForecastHour.kt
│   │   ├── SearchBar.kt
│   │   ├── TimeComponents.kt
│   │   ├── TopAppBar.kt
│   │   ├── TravelTimesCard.kt
│   │   └── WindDirectionArrow.kt
│   ├── screens/                  <- Én fil per navigasjonsdestinasjon
│   │   ├── AlertDetailScreen.kt
│   │   ├── ClothesScreen.kt
│   │   ├── ErrorScreen.kt
│   │   ├── HomeScreen.kt
│   │   ├── LocationForecastScreen.kt
│   │   ├── MapScreen.kt
│   │   └── SettingsScreen.kt
│   ├── theme/
│   │   ├── Color.kt
│   │   ├── Theme.kt
│   │   └── Type.kt
│   ├── util/
│   │   └── Format.kt             <- Formateringshjelpere (temperatur, tid, vind)
│   ├── MapApp.kt                 <- NavHost, Scaffold, nettverksovervåking
│   └── Routes.kt                 <- Navigasjonskonstanter og Destination-enum
│
└── viewmodel/                    <- Én ViewModel per skjerm
    ├── ClothesViewModel.kt
    ├── HomeViewModel.kt
    ├── LocationForecastViewmodel.kt
    ├── MapViewModel.kt
    ├── SearchViewModel.kt
    └── SettingsViewModel.kt
```
### Data
Vi har plassert datalaget i en egen mappe i prosjektet. Her har vi videre delt inn i egne mapper per datakilde. Hver av disse har som hovedregel minst en datasource og et repository, med unntak av weather-mappen, hvor vi kun har et repository.  
#### data/*/dto
I noen av undermappene for datakildene har vi også en mappe for data transfer objects. Dette er data-klasser som nøyaktig matcher responsformatet fra API-ene våre. 
#### data/*/mapper/
I locationforecast har vi også en mapper-mappe. Her har vi en mapper som transformerer DTO til domenemodellen vår. 
### di
I di-mappen ligger Hilt-moduler for dependency injection. 

### model
I model ligge domenmodellene våre. 
### ui
I ui-mappen ligger alle composables. 
#### ui/screens
Her ligger alle skjermene våre. En per destinasjon, i tillegg til en errorscreen som brukes på tvers av destinasjoner. 
#### ui/components
Her ligger gjenbrukbare ui-komponenter

### viewmodel
Her ligger alle viewmodelene våre, en per skjerm og en egen for søk. 

## MVVM og UDF
- UI samler tilstand med collectAsStateWithLifecycle() og sender brukerhandlinger som funksjonskall til ViewModel
- ViewModel eksponerer tilstand som StateFlow, aldri rå dataklasser eller suspend-funksjoner
- Repository er eneste inngang til data for ViewModels — ViewModels kjenner ikke til datakilder direkte. Unntaket er devicedatasource. 
- Datakilder håndterer HTTP-kall og caching (eks. If-Modified-Since i LocationForecastDataSource)

## Lav kobling, høy kohesjon
- Hilt sørger for at avhengigheter injiseres, ikke hardkodes — ingen klasser oppretter sine egne avhengigheter
- WeatherRepository er et interface; WeatherRepositoryImpl er den konkrete implementasjonen — lett å bytte ut eller mocke i tester
- Hver ViewModel kjenner kun til de repositoriene den trenger
- UI-komponenter mottar kun primitiver og callbacks, ikke ViewModels

## API-nivå
minSdk = 24 (Android 7.0) — dekker over 97 % av aktive enheter og gir tilgang til java.time via core library desugaring
targetSdk = 35 (Android 15)
Begrunnelse for valget: ...

## Teknologier 
Jetpack Compose - UI \
Hilt - Dependency injection \
Ktor CIO - HTTP-klienter \
kotlinx.serialization - JSON-parsing \
Navigation Compose - Navigasjon mellom skjermer \
DataStore Preferences - Lagring av brukerinnstillinger \
MapLibre - Kartvisning og WMS-kartlag \
Coil 3 + SVG-dekoder - Asynkron bildelasting (værsymboler) \
Play Services LocationGPS-posisjonering via FusedLocationProviderClient 

## For videreutvikling
- Alle MET-kall skal gå via IFI-proxyen (in2000.api.met.no), ikke direkte til api.met.no 
- ClothesViewModel abonnerer på DataStore direkte — endringer i innstillinger reflekteres automatisk uten manuell synkronisering 
- NetworkMonitor i MapApp styrer en wasOffline-flagg som brukes til å gjenoppta lasting etter nettverkstap 
- Nye skjermer legges til i NavHost i MapApp.kt og som konstant i Routes 
