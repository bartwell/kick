package ru.bartwell.kick.core.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
public fun ColumnScope.CheckboxWithText(
    text: String,
    isChecked: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .align(Alignment.End)
            .padding(horizontal = 8.dp)
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 8.dp),
            text = text,
        )
        Checkbox(
            checked = isChecked,
            onCheckedChange = null,
        )
    }
}
