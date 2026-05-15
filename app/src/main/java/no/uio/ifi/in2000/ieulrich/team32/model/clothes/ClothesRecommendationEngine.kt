package no.uio.ifi.in2000.ieulrich.team32.model.clothes

import no.uio.ifi.in2000.ieulrich.team32.model.locationForecast.ForecastHourDetails

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
    val wearTshirt: Boolean,
    val wearSweater: Boolean,
    val wearLightJacket: Boolean,
    val wearHeavyJacket: Boolean,
    val wearThermalUnderwear: Boolean,
    val wearShorts: Boolean,
    val wearPants: Boolean,
    val wearHatGloves: Boolean,
    val wearScarf: Boolean,
    val wearSunglasses: Boolean,
    val bringUmbrella: Boolean,
    val wearRainGear: Boolean,
    val wearWaterproofShoes: Boolean,
    val wearWinterBoots: Boolean,
    val wearSneakers: Boolean
)

object ClothesRecommendationEngine {

    fun recommend(
        forecasts: List<ForecastHourDetails>,
        settings: UserSettings,
        temperatureOffset: Float = 0f
    ): ClothesRecommendation? {
        if (forecasts.isEmpty()) return null

        val temperatures = forecasts.map { it.temperature }
        val baseTemp = if (settings.isOutdoors) {
            temperatures.average()
        } else {
            temperatures.min()
        }

        val cloudBonus = forecasts.minOf { cloudBonus(it.symbolCode) }

        val activityBonus = if (settings.isPhysicallyActive) {
            when (settings.activityLevel) {
                ActivityLevel.LOW -> 3
                ActivityLevel.MEDIUM -> 5
                ActivityLevel.HIGH -> 9
                null -> 0
            }
        } else 0

        val maxWind = forecasts.maxOf { it.windSpeed }
        val windPenalty = windPenalty(maxWind)

        val hoursOutdoors = if (settings.isOutdoors) {
            val h = settings.returnHour - settings.departureHour
            if (h < 0) h + 24 else h
        } else 0
        val isSunnyAndWarm = baseTemp > 20.0 && cloudBonus >= 3
        val outdoorPenalty = if (isSunnyAndWarm) 0 else outdoorPenalty(hoursOutdoors)

        // temperatureOffset legges til sist: viking (+5) → lettere klær, ispinne (-5) → tykkere
        val effectiveTemp = baseTemp + cloudBonus + activityBonus - windPenalty - outdoorPenalty + temperatureOffset

        val maxPrecipitation = forecasts.maxOf { it.precipitationAmount }
        val hasSun = forecasts.any { isSunny(it.symbolCode) }

        val shorts = effectiveTemp > 19.5
        val winterBoots = baseTemp < 0 && maxPrecipitation > 1.5
        val waterproofShoes = maxPrecipitation > 1.5 && baseTemp >= 0
        val sneakers = !winterBoots && !waterproofShoes

        return ClothesRecommendation(
            effectiveTemp = effectiveTemp,
            wearTshirt = effectiveTemp > 17.5,
            wearSweater = effectiveTemp in 14.5..17.5,
            wearLightJacket = effectiveTemp in 8.5..14.5,
            wearHeavyJacket = effectiveTemp < 8.5,
            wearThermalUnderwear = effectiveTemp < -5.5,
            wearShorts = shorts,
            wearPants = !shorts,
            wearHatGloves = effectiveTemp < 0.5,
            wearScarf = effectiveTemp < -1.5,
            wearSunglasses = hasSun,
            bringUmbrella = maxPrecipitation > 0.5 && maxWind < 5.5,
            wearRainGear = maxPrecipitation > 0.5 && maxWind >= 5.5,
            wearWinterBoots = winterBoots,
            wearWaterproofShoes = waterproofShoes,
            wearSneakers = sneakers
        )
    }

    private fun cloudBonus(symbolCode: String): Int {
        val code = symbolCode.lowercase()
        return when {
            code.startsWith("clearsky") -> 3
            code.startsWith("fair") || code.startsWith("partlycloudy") -> 1
            else -> 0
        }
    }

    private fun isSunny(symbolCode: String): Boolean {
        val code = symbolCode.lowercase()
        return code.startsWith("clearsky") || code.startsWith("fair")
    }

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

    private fun outdoorPenalty(hours: Int): Int = when {
        hours <= 0 -> 0
        hours < 2  -> 1
        hours <= 4 -> 3
        else       -> 5
    }


}