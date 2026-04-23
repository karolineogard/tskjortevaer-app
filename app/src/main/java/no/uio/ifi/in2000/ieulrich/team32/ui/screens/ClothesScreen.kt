package no.uio.ifi.in2000.ieulrich.team32.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.ieulrich.team32.R
import no.uio.ifi.in2000.ieulrich.team32.ui.Routes


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClothesScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {

    val sheetState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded,
            skipHiddenState = false
        )
    )

    val scope = rememberCoroutineScope()
    var hasNavigated by remember { mutableStateOf(false) }


    BackHandler(enabled = sheetState.bottomSheetState.currentValue == SheetValue.Expanded) {
        scope.launch {
            sheetState.bottomSheetState.partialExpand()
        }
    }

    BottomSheetScaffold(
        scaffoldState = sheetState,
        sheetPeekHeight = 64.dp,
        containerColor = MaterialTheme.colorScheme.background,
        sheetContent = {
            Text(
                text = "Tilpass klesanbefaling",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val morningTimeState = rememberTimePickerState(
                    initialHour = 8, initialMinute = 0, is24Hour = true
                )
                val eveningTimeState = rememberTimePickerState(
                    initialHour = 16, initialMinute = 0, is24Hour = true
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Når reiser du?", fontSize = 32.sp, fontWeight = FontWeight.Bold)

                        Text("Dra", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                        TimeInput(state = morningTimeState)

                        HorizontalDivider()

                        Text("Tilbake", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                        TimeInput(state = eveningTimeState)
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        CheckboxMinimalExample()
                    }
                }

                Button(
                    onClick = {
                        scope.launch { sheetState.bottomSheetState.partialExpand() }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Lagre")
                }
            }
        }
    ) { innerPadding ->


        LazyColumn(
            modifier = Modifier.padding(12.dp)
                .background(MaterialTheme.colorScheme.background)
            ,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 80.dp)


        ) {
            item {
                Text(
                    text = "Klær",
                    fontSize = 48.sp
                )

                Text(
                    text = "Anbefalningen tar utgangspunkt i fremkomst til og fra skole eller jobb. Vi antar at reisetidspunktet skjer mellom 8-10 på morgenen og 16-18 på kvelden. \n" +
                            "\n" +
                            "Swipe opp for å tilpasse klesanbefalingen!"
                )
            }
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {


                        Row(
                            modifier = Modifier
                                .padding(start = 8.dp, end = 8.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Image(
                                painter = painterResource(id = R.drawable.kl_r_ikon),
                                contentDescription = null,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))

                            Text(
                                text = "Detaljert klesanbefaling",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        HorizontalDivider()

                        Spacer(
                            modifier = Modifier
                                .size(15.dp)
                        )

                        Row(
                            modifier = Modifier
                                .padding(start = 8.dp, end = 8.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Image(
                                painter = painterResource(id = R.drawable.solbriller_ikon),
                                contentDescription = null,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))

                            Text(
                                text = "Hatt og/eller solbriller for å beskytte mot solen."
                            )
                        }
                        Spacer(
                            modifier = Modifier
                                .size(15.dp)
                        )

                        Row(
                            modifier = Modifier
                                .padding(start = 8.dp, end = 8.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Image(
                                painter = painterResource(id = R.drawable.caps_ikon),
                                contentDescription = null,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))

                            Text(
                                text = "Hatt og/eller solbriller for å beskytte mot solen."
                            )
                        }

                        Spacer(
                            modifier = Modifier
                                .size(15.dp)
                        )

                        Row(
                            modifier = Modifier
                                .padding(start = 8.dp, end = 8.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Image(
                                painter = painterResource(id = R.drawable.tskjorte_ikon),
                                contentDescription = null,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))

                            Text(
                                text = "dritvarmt ute, det er tskjorte vær."
                            )
                        }
                    }

                }

            }
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .padding(start = 8.dp, end = 8.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.paraply_ikon),
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))

                        Text(text = "Det kan være lurt å ta med paraply.")
                    }
                }


            }


        }
    }



}




