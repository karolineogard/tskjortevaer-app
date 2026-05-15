package no.uio.ifi.in2000.ieulrich.team32.ui.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import no.uio.ifi.in2000.ieulrich.team32.R
import no.uio.ifi.in2000.ieulrich.team32.model.metAlerts.MetAlert
import no.uio.ifi.in2000.ieulrich.team32.model.metAlerts.iconUrl
import no.uio.ifi.in2000.ieulrich.team32.ui.components.TopAppBar
import no.uio.ifi.in2000.ieulrich.team32.ui.theme.AlertYellow
import no.uio.ifi.in2000.ieulrich.team32.ui.theme.AlertOrange
import no.uio.ifi.in2000.ieulrich.team32.ui.theme.AlertRed
import no.uio.ifi.in2000.ieulrich.team32.ui.theme.RobotoMono
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertDetailScreen(
    alert: MetAlert,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                alert.eventAwarenessName ?: stringResource(R.string.alert_title),
                onBack
            )
        }
    ) { innerPadding ->
        Log.d("MetAlert", "severity: ${alert.severity}")

        Column(
            modifier = Modifier
                .padding(top = innerPadding.calculateTopPadding())
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = severityColor(alert.severity).copy(alpha = 0.3f)
                ),
                border = BorderStroke(2.dp, severityColor(alert.severity))
            ){
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AsyncImage(
                            model = alert.iconUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(86.dp)
                                .padding(end = 16.dp),
                            contentScale = ContentScale.Fit
                        )
                        Text(
                            text = stringResource(
                                R.string.alert_ongoing,
                                alert.eventAwarenessName ?: stringResource(R.string.alert_title)
                            ),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            fontFamily = RobotoMono,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    alert.timeInterval?.let { interval ->
                        val from = formatAlertTime(interval.validFrom)
                        val to = formatAlertTime(interval.validTo)
                        InfoSection(
                            label = stringResource(R.string.alert_validity),
                            value = "$from – $to",
                        )
                    }
                    alert.severity?.let {
                        InfoSection(
                            label = stringResource(R.string.alert_severity),
                            value = formatSeverity(it)
                        )
                    }

                    alert.title?.let {
                        InfoSection(
                            label = stringResource(R.string.alert_title),
                            value = "${alert.eventAwarenessName} – ${alert.area ?: ""}"
                        )
                    }

                    alert.area?.let {
                        InfoSection(
                            label = stringResource(R.string.alert_area),
                            value = it
                        )
                    }
                    alert.description?.let {
                        InfoSection(
                            label = stringResource(R.string.alert_description),
                            value = it
                        )
                    }
                    alert.consequences?.let {
                        InfoSection(
                            label = stringResource(R.string.alert_consequence),
                            value = it
                        )
                    }
                    alert.instruction?.let {
                        InfoSection(
                            label = stringResource(R.string.alert_instruction),
                            value = it
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(2.dp))
                    SeverityLegend()
                    Spacer(modifier = Modifier.height(4.dp))

                }

            }

        }
    }
}

@Composable
fun InfoSection(label: String, value: String, color: Color = MaterialTheme.colorScheme.onPrimary) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = color,
            fontFamily = RobotoMono,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            fontFamily = RobotoMono,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}


fun severityColor(severity: String?): Color = when (severity?.lowercase()){
    "moderate" -> AlertYellow
    "severe"   -> AlertOrange
    "extreme"  -> AlertRed
    else -> Color.LightGray
}

fun formatSeverity(severity: String?): String = when (severity?.lowercase()) {
    "moderate" -> "Gul"
    "severe"   -> "Oransje"
    "extreme"  -> "Rød"
    else       -> severity ?: "Ukjent"
}
fun formatAlertTime(isoString: String?): String {
    if (isoString == null) return "?"
    return try {
        val odt = java.time.OffsetDateTime.parse(isoString)
        val zoned = odt.atZoneSameInstant(java.time.ZoneId.of("Europe/Oslo"))
        val formatter = java.time.format.DateTimeFormatter.ofPattern("EEEE d. MMM HH:mm", Locale.forLanguageTag("nb"))
        zoned.format(formatter)
    } catch (_: Exception) {
        isoString
    }
}
@Composable
fun SeverityLegend(modifier: Modifier = Modifier) {
    val levels = listOf(
        Triple(
            "minor",
            AlertYellow,
            stringResource(R.string.alert_yellow)
        ),
        Triple(
            "moderate",
            AlertOrange,
            stringResource(R.string.alert_orange)
        ),
        Triple(
            "severe",
            AlertRed,
            stringResource(R.string.alert_red)
        ),
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = stringResource(R.string.alert_levels),
            fontFamily = RobotoMono,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary
        )
        levels.forEach { (_, color, label) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(color)
                        .border(1.dp, Color.Black.copy(alpha = 0.3f), RoundedCornerShape(3.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    fontFamily = RobotoMono,
                    //style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}