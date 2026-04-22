package no.uio.ifi.in2000.ieulrich.team32.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.util.Calendar
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import no.uio.ifi.in2000.ieulrich.team32.ui.Routes


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdjustmentScreen(
    modifier: Modifier = Modifier,
    navController: NavController
){

    val currentTime = Calendar.getInstance()

    val morningTimeState = rememberTimePickerState(
        initialHour = 8,
        initialMinute = 0,
        is24Hour = true
    )

    val eveningTimeState = rememberTimePickerState(
        initialHour = 16,
        initialMinute = 0,
        is24Hour = true
    )


    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Justeringer", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Tilbake"
                        )
                    }
                },
                expandedHeight = 32.dp,
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background // samme farge
                )

            )

        }
    ){

        innerPadding ->

        Column(modifier = Modifier
            .padding(innerPadding)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface // hvit i ditt scheme
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Når reiser du?",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )

                    val morningTimeState =
                        rememberTimePickerState(initialHour = 8, initialMinute = 0, is24Hour = true)
                    val eveningTimeState = rememberTimePickerState(
                        initialHour = 16,
                        initialMinute = 0,
                        is24Hour = true
                    )

                    Text("Dra", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                    TimeInput(state = morningTimeState)

                    HorizontalDivider()

                    Text("Tilbake", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                    TimeInput(state = eveningTimeState)

                }
            }
            //Spacer(modifier = Modifier.size(8.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface // hvit i ditt scheme
                )
            ){Column(modifier = Modifier.padding(16.dp)) {
                CheckboxMinimalExample()
            }
            }

            Button(
                onClick = { navController.navigate(Routes.CLOTHES) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Lagre")
            }





        }


        }
    }


enum class ActivityLevel { LOW, MEDIUM, HIGH }

@Composable
fun CheckboxMinimalExample() {
    var outdoors by remember { mutableStateOf(false) }
    var physically by remember { mutableStateOf(false) }
    var activityLevel by remember { mutableStateOf<ActivityLevel?>(null) }



    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Skal du være utendørs")
        Checkbox(
            checked = outdoors,
            onCheckedChange = {
                outdoors = it
                if (!it) physically = false
                activityLevel = null
            }
        )
    }

    if (outdoors) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Skal du være fysisk aktiv")
                Checkbox(
                    checked = physically,
                    onCheckedChange = {
                        physically = it
                        if (!it) activityLevel = null
                    }
                )
            }
        }
    }

    if (physically) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface // hvit i ditt scheme
            )
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Hvilket nivå fysisk aktiv kommer du til å være (i snitt)?")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Lavt")
                    Checkbox(
                        checked = activityLevel == ActivityLevel.LOW,
                        onCheckedChange = { activityLevel = if (it) ActivityLevel.LOW else null }
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Middels")
                    Checkbox(
                        checked = activityLevel == ActivityLevel.MEDIUM,
                        onCheckedChange = { activityLevel = if (it) ActivityLevel.MEDIUM else null }
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Høy")
                    Checkbox(
                        checked = activityLevel == ActivityLevel.HIGH,
                        onCheckedChange = { activityLevel = if (it) ActivityLevel.HIGH else null }
                    )
                }
            }

        }
    }
}




