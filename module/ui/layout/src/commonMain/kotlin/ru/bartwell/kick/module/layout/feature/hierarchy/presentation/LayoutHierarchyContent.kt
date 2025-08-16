package ru.bartwell.kick.module.layout.feature.hierarchy.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.bartwell.kick.core.presentation.LocalAppUiEnvironment
import ru.bartwell.kick.module.layout.core.data.LayoutNodeId
import ru.bartwell.kick.module.layout.core.data.LayoutNodeSnapshot

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LayoutHierarchyContent(
    component: LayoutHierarchyComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.model.subscribeAsState()
    Column(modifier = modifier) {
        TopAppBar(
            title = { Text("Layout Hierarchy") },
            navigationIcon = {
                IconButton(onClick = LocalAppUiEnvironment.current.screenCloser) {
                    Icon(imageVector = Icons.Outlined.Close, contentDescription = "Close")
                }
            }
        )
        state.root?.let { root ->
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                NodeView(node = root, depth = 0, onNodeSelected = component::onNodeSelected)
            }
        }
    }
}

@Composable
private fun NodeView(
    node: LayoutNodeSnapshot,
    depth: Int,
    onNodeSelected: (LayoutNodeId) -> Unit,
) {
    var expanded by remember { mutableStateOf(true) }

    Column(modifier = Modifier.padding(start = (depth * 8).dp)) {
        androidx.compose.foundation.layout.Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            if (node.children.isNotEmpty()) {
                IconButton(onClick = { expanded = !expanded }) {
                    val icon = if (expanded) {
                        Icons.Filled.KeyboardArrowDown
                    } else {
                        Icons.AutoMirrored.Filled.KeyboardArrowRight
                    }
                    Icon(imageVector = icon, contentDescription = null)
                }
            } else {
                Spacer(modifier = Modifier.size(40.dp))
            }
            Text(
                text = node.displayName,
                modifier = Modifier.clickable { onNodeSelected(node.id) }
            )
        }
        if (expanded) {
            node.children.forEach { child ->
                NodeView(node = child, depth = depth + 1, onNodeSelected = onNodeSelected)
            }
        }
    }
}
