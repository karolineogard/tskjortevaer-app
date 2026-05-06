package no.uio.ifi.in2000.ieulrich.team32.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class ActivityLevel(val displayValue: String) {
    LOW("Lav"),
    MEDIUM("Medium"),
    HIGH("Høy")
}

@Composable
fun TimeInputField(
    initialHour: Int = 8,
    initialMinute: Int = 0,
    onTimeChanged: ((hour: Int, minute: Int) -> Unit)? = null
) {
    var hour by remember { mutableIntStateOf(initialHour) }
    var minute by remember { mutableIntStateOf(initialMinute) }
    val hourFocus = remember { FocusRequester() }
    val minuteFocus = remember { FocusRequester() }
    var hourFocused by remember { mutableStateOf(false) }
    var minuteFocused by remember { mutableStateOf(false) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        TimeBox(
            displayValue = hour.toString().padStart(2, '0'),
            isFocused = hourFocused,
            focusRequester = hourFocus,
            onFocusChanged = { hourFocused = it },
            onValueChange = { newText ->
                val digit = findNewDigit(hour.toString().padStart(2, '0'), newText)
                if (digit == null) hour /= 10 else {
                    val next = (hour % 10) * 10 + digit
                    if (next in 0..23) hour = next else if (digit in 0..2) hour = digit
                }
                onTimeChanged?.invoke(hour, minute)
            }
        )
        Text(" : ", fontSize = 28.sp)
        TimeBox(
            displayValue = minute.toString().padStart(2, '0'),
            isFocused = minuteFocused,
            focusRequester = minuteFocus,
            onFocusChanged = { minuteFocused = it },
            onValueChange = { newText ->
                val digit = findNewDigit(minute.toString().padStart(2, '0'), newText)
                if (digit == null) minute /= 10 else {
                    val next = (minute % 10) * 10 + digit
                    if (next in 0..59) minute = next else if (digit in 0..5) minute = digit
                }
                onTimeChanged?.invoke(hour, minute)
            }
        )
    }
}

@Composable
private fun TimeBox(
    displayValue: String,
    isFocused: Boolean,
    focusRequester: FocusRequester,
    onFocusChanged: (Boolean) -> Unit,
    onValueChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .width(80.dp)
            .height(72.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(
                2.dp,
                if (isFocused) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(0.4f),
                RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = TextFieldValue(displayValue, selection = TextRange(displayValue.length)),
            onValueChange = { onValueChange(it.text) },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged { onFocusChanged(it.isFocused) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            textStyle = TextStyle(
                fontSize = 32.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            ),
            decorationBox = { inner -> Box(contentAlignment = Alignment.Center) { inner() } }
        )
    }
}

private fun findNewDigit(old: String, new: String): Int? {
    val oldDigits = old.filter { it.isDigit() }
    val newDigits = new.filter { it.isDigit() }
    if (newDigits.length <= oldDigits.length) return null
    return newDigits.last().digitToInt()
}

@Composable
fun CheckboxSection(
    isOutdoors: Boolean,
    onOutdoorsChange: (Boolean) -> Unit,
    isPhysical: Boolean,
    onPhysicalChange: (Boolean) -> Unit,
    activityLevel: ActivityLevel?,
    onActivityLevelChange: (ActivityLevel?) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Skal du være utendørs")
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = isOutdoors, onCheckedChange = {
            onOutdoorsChange(it)
            if (!it) {
                onPhysicalChange(false)
                onActivityLevelChange(null)
            }
        })
        Text("Ja")
        Spacer(modifier = Modifier.width(16.dp))
        Checkbox(checked = !isOutdoors, onCheckedChange = {
            onOutdoorsChange(!it)
            if (it) {
                onPhysicalChange(false)
                onActivityLevelChange(null)
            }
        })
        Text("Nei")
    }
}