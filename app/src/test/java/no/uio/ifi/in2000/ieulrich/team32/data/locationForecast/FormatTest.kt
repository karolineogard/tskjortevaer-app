package no.uio.ifi.in2000.ieulrich.team32.data.locationForecast

import no.uio.ifi.in2000.ieulrich.team32.ui.util.Format.extractDate
import no.uio.ifi.in2000.ieulrich.team32.ui.util.Format.extractHour
import no.uio.ifi.in2000.ieulrich.team32.ui.util.Format.extractTime
import no.uio.ifi.in2000.ieulrich.team32.ui.util.Format.formatTemp
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class FormatTest {
    // alle testene som har med tid å gjøre her har en svakhet i at de kun vil passere i samme tidssone.
    @Test
    fun extractHourShouldAssertTrue() {
        // Arrange
        val timestamp = "2026-04-07T10:00:00Z"

        // Act
        val result = extractHour(timestamp)

        // Assert
        assertEquals("12", result)
    }

    @Test
    fun extractHourShouldAssertFalse() {
        // Arrange
        val timestamp = "2026-04-07T14:00:00Z"

        // Act
        val result = extractHour(timestamp)

        // Assert
        assertNotEquals("12", result)
    }

    @Test
    fun formatTempShouldAssertTrue() {
        // Arrange
        val temp = 24.5

        // Act
        val result = formatTemp(temp)

        // Assert
        assertEquals("25°", result)
    }

    @Test
    fun formatTempShouldAssertFalse() {
        // Arrange
        val temp = 24.4

        // Act
        val result = formatTemp(temp)

        // Assert
        assertNotEquals("25°", result)
    }

    @Test
    fun extractDateShouldAssertTrue() {
        // Arrange
        val date = "2026-05-01T23:00:00Z"
        // Act
        val result = extractDate(date)
        // Assert
        assertEquals("Lørdag 2. mai", result)
    }

    @Test
    fun extractDateShouldAssertFalse() {
        // Arrange
        val date = "2026-05-01T23:00:00Z"
        // Act
        val result = extractDate(date)
        // Assert
        assertNotEquals("Fredag 1. mai", result)
    }

    @Test
    fun extractTimeShouldAssertTrue() {
        // Arrange
        val time = "2026-05-01T13:00:00Z"
        // Act
        val result = extractTime(time)
        // Assert
        assertEquals("15:00", result)
    }

    @Test
    fun extractTimeShouldAssertFalse() {
        // Arrange
        val time = "2026-05-01T13:00:00Z"
        // Act
        val result = extractTime(time)
        // Assert
        assertNotEquals("13:00", result)
    }

}