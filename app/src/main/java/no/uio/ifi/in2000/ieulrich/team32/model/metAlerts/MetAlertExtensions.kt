package no.uio.ifi.in2000.ieulrich.team32.model.metAlerts


val MetAlert.iconUrl: String
    get() {
        val baseUrl = "https://raw.githubusercontent.com/nrkno/yr-warning-icons/master/design/svg/icon-warning-"
        val eventName = when (this.event?.lowercase()) {
            "forestfire" -> "forestfire-"
            "ice" -> "ice-"
            "gale" -> "wind-"
            "wind" -> "wind-"
            "lightning" -> "lightning-"
            "polarlow" -> "polarlow-"
            "rain" -> "rain-"
            "rainflood" -> "rainflood-"
            "snow" -> "snow-"
            "stormsurge" -> "stormsurge-"
            "blowingsnow" -> "snow-"

            else -> "generic-"
        }
        val severityName = when (this.severity?.lowercase()) {
            "moderate" -> "yellow.svg"
            "severe" -> "orange.svg"
            "extreme" -> "red.svg"
            else -> "orange.svg"
        }
        return "$baseUrl$eventName$severityName"
    }
