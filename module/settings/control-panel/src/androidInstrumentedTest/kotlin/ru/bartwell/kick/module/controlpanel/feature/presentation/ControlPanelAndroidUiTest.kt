package ru.bartwell.kick.module.controlpanel.feature.presentation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.bartwell.kick.module.controlpanel.data.ControlPanelItem
import ru.bartwell.kick.module.controlpanel.data.Editor
import ru.bartwell.kick.module.controlpanel.data.InputType

private const val MIN_VALUE = 0.0
private const val MAX_VALUE = 100.0
private const val DEFAULT_MAX_ITEMS = 5

@RunWith(AndroidJUnit4::class)
class ControlPanelAndroidUiTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun interactions() {
        val items = listOf(
            ControlPanelItem(
                name = "FeatureFlag",
                type = InputType.Boolean(false),
                editor = null,
            ),
            ControlPanelItem(
                name = "MaxItems",
                type = InputType.Int(DEFAULT_MAX_ITEMS),
                editor = Editor.InputNumber(
                    min = MIN_VALUE,
                    max = MAX_VALUE,
                ),
            ),
            ControlPanelItem(
                name = "Mode",
                type = InputType.String("A"),
                editor = Editor.InputString(singleLine = true),
            )
        )
        val fake = FakeControlPanelComponent(items)

        composeRule.setContent { ControlPanelContent(component = fake) }

        composeRule.onNodeWithTag("back").performClick()
        composeRule.onNodeWithTag("save").performClick()
        assertTrue(fake.backInvoked)
        assertTrue(fake.saveInvoked)

        val toggle = composeRule.onNodeWithTag("bool_FeatureFlag")
        toggle.assertIsOff()
        toggle.performClick()
        toggle.assertIsOn()

        val numberInput = composeRule.onNodeWithTag("number_input_MaxItems")
        numberInput.performTextInput("42")
        composeRule.onNodeWithText("MaxItems").assertExists()

        val stringInput = composeRule.onNodeWithTag("string_input_Mode")
        stringInput.performTextInput("B")

        composeRule.onAllNodesWithTag("config_list").assertCountEquals(1)
    }
}
