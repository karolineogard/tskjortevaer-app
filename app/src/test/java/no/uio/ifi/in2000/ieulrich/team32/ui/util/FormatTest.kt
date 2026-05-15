package no.uio.ifi.in2000.ieulrich.team32.ui.util

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class FormatTest {
    // alle testene som har med tid å gjøre her har en svakhet i at de kun vil passere i samme tidssone.
    @Test
    fun extractHourShouldAssertTrue() {
        // Arrange
        val timestamp = "2026-04-07T10:00:00Z"

        // Act
        val result = Format.extractHour(timestamp)

        // Assert
        Assertions.assertEquals("12", result)
    }

    @Test
    fun extractHourShouldAssertFalse() {
        // Arrange
        val timestamp = "2026-04-07T14:00:00Z"

        // Act
        val result = Format.extractHour(timestamp)

        // Assert
        Assertions.assertNotEquals("12", result)
    }

    @Test
    fun formatTempShouldAssertTrue() {
        // Arrange
        val temp = 24.5

        // Act
        val result = Format.formatTemp(temp)

        // Assert
        Assertions.assertEquals("25°", result)
    }

    @Test
    fun formatTempShouldAssertFalse() {
        // Arrange
        val temp = 24.4

        // Act
        val result = Format.formatTemp(temp)

        // Assert
        Assertions.assertNotEquals("25°", result)
    }

    @Test
    fun extractDateShouldAssertTrue() {
        // Arrange
        val date = "2026-05-01T23:00:00Z"
        // Act
        val result = Format.extractDate(date)
        // Assert
        Assertions.assertEquals("Lørdag 2. mai", result)
    }

    @Test
    fun extractDateShouldAssertFalse() {
        // Arrange
        val date = "2026-05-01T23:00:00Z"
        // Act
        val result = Format.extractDate(date)
        // Assert
        Assertions.assertNotEquals("Fredag 1. mai", result)
    }

    @Test
    fun extractTimeShouldAssertTrue() {
        // Arrange
        val time = "2026-05-01T13:00:00Z"
        // Act
        val result = Format.extractTime(time)
        // Assert
        Assertions.assertEquals("15:00", result)
    }

    @Test
    fun extractTimeShouldAssertFalse() {
        // Arrange
        val time = "2026-05-01T13:00:00Z"
        // Act
        val result = Format.extractTime(time)
        // Assert
        Assertions.assertNotEquals("13:00", result)
    }

}