package no.uio.ifi.in2000.ieulrich.team32.model.clothes

import no.uio.ifi.in2000.ieulrich.team32.model.locationForecast.ForecastHourDetails
import no.uio.ifi.in2000.ieulrich.team32.ui.components.ActivityLevel

data class UserSettings(
    val departureHour: Int = 8,
    val departureMinute: Int = 0,
    val returnHour: Int = 16,
    val returnMinute: Int = 0,
    val isOutdoors: Boolean = false,
    val isPhysicallyActive: Boolean = false,
    val activityLevel: ActivityLevel? = null
)

data class ClothesRecommendation(
    val effectiveTemp: Double,
    // Overkropp (alltid én aktiv)
    val wearTshirt: Boolean,
    val wearSweater: Boolean,
    val wearLightJacket: Boolean,
    val wearHeavyJacket: Boolean,
    val wearThermalUnderwear: Boolean,
    // Underkropp (alltid én aktiv)
    val wearShorts: Boolean,
    val wearPants: Boolean,
    // Hode/hals
    val wearHatGloves: Boolean,
    val wearScarf: Boolean,
    val wearSunglasses: Boolean,
    // Regn/vind
    val bringUmbrella: Boolean,
    val wearRainGear: Boolean,
    // Sko (alltid én aktiv)
    val wearWaterproofShoes: Boolean,
    val wearWinterBoots: Boolean,
    val wearSneakers: Boolean
)

object ClothesRecommendationEngine {

    /**
     * Beregner effektiv temperatur basert på poengsystemet og returnerer klesanbefaling.
     *
     * @param forecasts Timedata for den relevante perioden (dra → tilbake)
     * @param settings  Brukerens innstillinger
     */
    fun recommend(
        forecasts: List<ForecastHourDetails>,
        settings: UserSettings
    ): ClothesRecommendation {
        if (forecasts.isEmpty()) return defaultRecommendation()

        // --- Temperatur ---
        // Bruker gjennomsnitt hvis utendørs hele perioden, ellers worst-case (kaldest)
        val temperatures = forecasts.map { it.rawTemperature() }
        val baseTemp = if (settings.isOutdoors) {
            temperatures.average()
        } else {
            temperatures.min()
        }

        // --- Skydekke-poeng (basert på symbolCode, følger Yr-logikk) ---
        // Bruker worst-case (lavest poeng) i perioden
        val cloudBonus = forecasts.minOf { cloudBonus(it.symbolCode) }

        // --- Aktivitetspoeng ---
        val activityBonus = if (settings.isPhysicallyActive) {
            when (settings.activityLevel) {
                ActivityLevel.LOW -> 3
                ActivityLevel.MEDIUM -> 5
                ActivityLevel.HIGH -> 9
                null -> 0
            }
        } else 0

        // --- Vindpoeng (minus) ---
        // Worst-case: sterkeste vind i perioden
        val maxWind = forecasts.maxOf { it.rawWindSpeed() }
        val windPenalty = windPenalty(maxWind)

        // --- Tid utendørs-poeng (minus) ---
        // Teller ikke hvis over 20 grader og sol
        val hoursOutdoors = if (settings.isOutdoors) {
            val h = settings.returnHour - settings.departureHour
            if (h < 0) h + 24 else h
        } else 0
        val isSunnyAndWarm = baseTemp > 20.0 && cloudBonus >= 3
        val outdoorPenalty = if (isSunnyAndWarm) 0 else outdoorPenalty(hoursOutdoors)

        // --- Effektiv temperatur ---
        val effectiveTemp = baseTemp + cloudBonus + activityBonus - windPenalty - outdoorPenalty

        // --- Nedbør: worst-case i hele perioden ---
        val maxPrecipitation = forecasts.maxOf { it.rawPrecipitation() }

        // --- Sol (for solbriller) ---
        val hasSun = forecasts.any { isSunny(it.symbolCode) }

        // --- Klesanbefaling basert på grenseverdier ---
        val shorts = effectiveTemp > 19.5
        val winterBoots = baseTemp < 0 && maxPrecipitation > 1.5
        val waterproofShoes = maxPrecipitation > 1.5 && baseTemp >= 0
        val sneakers = !winterBoots && !waterproofShoes  // alltid anbefalt hvis ingen spesialsko

        return ClothesRecommendation(
            effectiveTemp = effectiveTemp,

            // Overkropp – kun én av disse vil typisk være aktiv
            wearTshirt = effectiveTemp > 17.5,
            wearSweater = effectiveTemp in 14.5..17.5,
            wearLightJacket = effectiveTemp in 8.5..14.5,
            wearHeavyJacket = effectiveTemp < 8.5,
            wearThermalUnderwear = effectiveTemp < -5.5,

            // Underkropp – alltid én aktiv
            wearShorts = shorts,
            wearPants = !shorts,

            // Hode/hals
            wearHatGloves = effectiveTemp < 0.5,
            wearScarf = effectiveTemp < -1.5,
            wearSunglasses = hasSun,

            // Regn/vind
            bringUmbrella = maxPrecipitation > 0.5 && maxWind < 5.5,
            wearRainGear = maxPrecipitation > 0.5 && maxWind >= 5.5,

            // Sko – alltid én aktiv
            wearWinterBoots = winterBoots,
            wearWaterproofShoes = waterproofShoes,
            wearSneakers = sneakers
        )
    }

    // --- Hjelpefunksjoner ---

    /** Henter rå temperatur som Double fra ForecastHourDetails (fjerner °-tegnet) */
    private fun ForecastHourDetails.rawTemperature(): Double =
        temperature

    /** Henter rå vindhastighet som Double (fjerner " m/s") */
    private fun ForecastHourDetails.rawWindSpeed(): Double =
        windSpeed

    /** Henter rå nedbør som Double */
    private fun ForecastHourDetails.rawPrecipitation(): Double =
        precipitationAmount

    /**
     * Skydekke-bonus basert på symbolCode.
     * Følger Yr-logikk: clearsky = +3, fair/partlycloudy = +1, resten = 0
     */
    private fun cloudBonus(symbolCode: String): Int {
        val code = symbolCode.lowercase()
        return when {
            code.startsWith("clearsky") -> 3
            code.startsWith("fair") || code.startsWith("partlycloudy") -> 1
            else -> 0
        }
    }

    /** Om det er sol (for solbrille-anbefaling) */
    private fun isSunny(symbolCode: String): Boolean {
        val code = symbolCode.lowercase()
        return code.startsWith("clearsky") || code.startsWith("fair") || code.startsWith("partlycloudy")
    }

    /**
     * Vindstraff i poeng basert på internasjonale grenseverdier (m/s).
     */
    private fun windPenalty(windSpeed: Double): Int = when {
        windSpeed < 1.5  -> 0
        windSpeed < 3.0  -> 1
        windSpeed < 4.5  -> 2
        windSpeed < 6.0  -> 3
        windSpeed < 9.0  -> 4
        windSpeed < 12.0 -> 5
        windSpeed < 16.5 -> 6
        windSpeed < 22.5 -> 7
        else             -> 8
    }

    /**
     * Straff for å være ute hele dagen.
     */
    private fun outdoorPenalty(hours: Int): Int = when {
        hours <= 0 -> 0
        hours < 2  -> 1
        hours <= 4 -> 3
        else       -> 5
    }

    private fun defaultRecommendation() = ClothesRecommendation(
        effectiveTemp = 10.0,
        wearTshirt = false, wearSweater = false, wearLightJacket = true,
        wearHeavyJacket = false, wearThermalUnderwear = false,
        wearShorts = false, wearPants = true,
        wearHatGloves = false, wearScarf = false,
        wearSunglasses = false, bringUmbrella = false, wearRainGear = false,
        wearWaterproofShoes = false, wearWinterBoots = false, wearSneakers = true
    )
}