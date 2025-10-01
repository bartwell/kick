package ru.bartwell.kick.module.controlpanel.feature.presentation

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test
import ru.bartwell.kick.module.controlpanel.data.ControlPanelItem
import ru.bartwell.kick.module.controlpanel.data.Editor
import ru.bartwell.kick.module.controlpanel.data.InputType
import kotlin.test.assertTrue

@Suppress("FunctionNaming")
class ControlPanelUiTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun back_and_save_and_toggle_and_number_and_string() {
        val items = listOf(
            ControlPanelItem("FeatureFlag", InputType.Boolean(false), null),
            ControlPanelItem("MaxItems", InputType.Int(5), Editor.InputNumber(min = 0.0, max = 100.0)),
            ControlPanelItem("Mode", InputType.String("A"), Editor.InputString(singleLine = true))
        )
        val fake = FakeControlPanelComponent(items)

        composeRule.setContent { ControlPanelContent(component = fake) }

        // Back and Save buttons exist and clickable
        composeRule.onNodeWithTag("back").performClick()
        composeRule.onNodeWithTag("save").performClick()
        assertTrue(fake.backInvoked)
        assertTrue(fake.saveInvoked)

        // Boolean toggle
        val toggle = composeRule.onNodeWithTag("bool_FeatureFlag")
        toggle.assertIsOff()
        toggle.performClick()
        toggle.assertIsOn()

        // Number input change
        val numberInput = composeRule.onNodeWithTag("number_input_MaxItems")
        numberInput.performTextInput("42")
        composeRule.onNodeWithText("MaxItems").assertExists()

        // String input change
        val stringInput = composeRule.onNodeWithTag("string_input_Mode")
        stringInput.performTextInput("B")

        // List contains all three items
        composeRule.onAllNodesWithTag("config_list").assertCountEquals(1)
    }
}
