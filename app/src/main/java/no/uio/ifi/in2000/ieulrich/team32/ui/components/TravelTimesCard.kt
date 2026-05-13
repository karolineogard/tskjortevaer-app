package no.uio.ifi.in2000.ieulrich.team32.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.ieulrich.team32.R

@Composable
fun TravelTimesCard(
    departureHour: Int,
    departureMinute: Int,
    returnHour: Int,
    returnMinute: Int,
    onDepartureTimeChanged: (hour: Int, minute: Int) -> Unit,
    onReturnTimeChanged: (hour: Int, minute: Int) -> Unit,
    title: String
){
    var showTimeInfo by remember { mutableStateOf(false) }

    Box {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { showTimeInfo = true } ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = stringResource(R.string.info_button),
                            modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    stringResource(R.string.settings_leave),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                TimeInputField(
                    initialHour = departureHour,
                    initialMinute = departureMinute,
                    onTimeChanged = onDepartureTimeChanged
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                Text(
                    stringResource(R.string.settings_return),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                TimeInputField(
                    initialHour = returnHour,
                    initialMinute = returnMinute,
                    onTimeChanged = onReturnTimeChanged
                )
            }
        }

        if (showTimeInfo) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) { Text(
                            text = stringResource(R.string.settings_traveltime_info_title),
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.weight(1f),

                        )
                        IconButton(
                            onClick = { showTimeInfo = false },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Lukk",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = stringResource(R.string.settings_traveltime_info),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewTravelTimesCard(){
    TravelTimesCard(
        departureHour = 12,
        departureMinute = 30,
        returnHour = 15,
        returnMinute = 45,
        onDepartureTimeChanged = { _, _ -> },
        onReturnTimeChanged = { _, _ -> },
        title = "Når drar du?"
    )
}