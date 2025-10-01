package ru.bartwell.kick.module.configuration.feature.presentation

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import ru.bartwell.kick.module.configuration.data.ConfigurationItem
import ru.bartwell.kick.module.configuration.data.Editor
import ru.bartwell.kick.module.configuration.data.ValueType
import kotlin.test.Test
import kotlin.test.assertTrue

private const val MIN_VALUE = 0.0
private const val MAX_VALUE = 100.0
private const val DEFAULT_MAX_ITEMS = 5

@OptIn(ExperimentalTestApi::class)
class ConfigurationWasmUiTest {
    @Test
    fun interactions() = runComposeUiTest {
        val items = listOf(
            ConfigurationItem(
                name = "FeatureFlag",
                default = ValueType.Boolean(false),
                editor = null,
            ),
            ConfigurationItem(
                name = "MaxItems",
                default = ValueType.Int(DEFAULT_MAX_ITEMS),
                editor = Editor.InputNumber(
                    min = MIN_VALUE,
                    max = MAX_VALUE,
                ),
            ),
            ConfigurationItem(
                name = "Mode",
                default = ValueType.String("A"),
                editor = Editor.InputString(singleLine = true),
            )
        )
        val fake = FakeConfigurationComponent(items)
        setContent { ConfigurationContent(component = fake) }

        onNodeWithTag("back").performClick()
        onNodeWithTag("save").performClick()
        assertTrue(fake.backInvoked)
        assertTrue(fake.saveInvoked)

        val toggle = onNodeWithTag("bool_FeatureFlag")
        toggle.assertIsOff()
        toggle.performClick()
        toggle.assertIsOn()

        onNodeWithTag("number_input_MaxItems").performTextInput("42")
        onNodeWithText("MaxItems").assertExists()

        val stringInput = onNodeWithTag("string_input_Mode")
        stringInput.performTextInput("B")

        onAllNodesWithTag("config_list").assertCountEquals(1)
    }
}
