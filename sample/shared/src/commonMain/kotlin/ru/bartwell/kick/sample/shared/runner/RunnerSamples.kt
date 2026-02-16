package ru.bartwell.kick.sample.shared.runner

import ru.bartwell.kick.Kick
import ru.bartwell.kick.core.util.DateUtils
import ru.bartwell.kick.module.runner.core.data.PlatformImage
import ru.bartwell.kick.module.runner.core.params.RunnerParameter
import ru.bartwell.kick.module.runner.core.params.RunnerParameterType
import ru.bartwell.kick.module.runner.core.renderer.ImageRunnerRenderer
import ru.bartwell.kick.module.runner.core.renderer.JsonRunnerRenderer
import ru.bartwell.kick.module.runner.core.renderer.ObjectRunnerRenderer
import ru.bartwell.kick.module.runner.runner

internal fun registerRunnerSamples() {
    Kick.runner.addCall(
        title = "JSON sample",
        description = "Pretty-printed JSON output",
        renderer = JsonRunnerRenderer(),
    ) {
        """{"message":"Hello, Runner!","timestamp":${DateUtils.currentTimeMillis()}}"""
    }

    Kick.runner.addCall(
        title = "Object sample",
        description = "Any object via toString()",
        renderer = ObjectRunnerRenderer(),
    ) {
        SampleObject(id = 42, name = "Runner", flag = true)
    }

    Kick.runner.addCall(
        title = "Image sample",
        description = "PlatformImage rendered with ImageRunnerRenderer",
        renderer = ImageRunnerRenderer(),
    ) {
        createSamplePlatformImage()
    }

    Kick.runner.addCall(
        title = "Parameterized sample",
        description = "Shows how to run with typed params",
        params = listOf(
            RunnerParameter(
                id = "count",
                title = "Count",
                description = "1..5",
                required = true,
                type = RunnerParameterType.IntType(min = 1, max = 5),
                defaultValue = 1,
            ),
            RunnerParameter(
                id = "label",
                title = "Label",
                description = "Any text",
                type = RunnerParameterType.StringType(),
                defaultValue = "demo",
            ),
            RunnerParameter(
                id = "flags",
                title = "Flags",
                description = "Select multiple",
                type = RunnerParameterType.MultiChoice(options = listOf("A", "B", "C")),
                defaultValue = setOf("A"),
            ),
            RunnerParameter(
                id = "option",
                title = "Option",
                description = "Select one",
                type = RunnerParameterType.SingleChoice(options = listOf("One", "Two", "Three")),
                defaultValue = "One",
            ),
        ),
        renderer = ObjectRunnerRenderer(),
    ) { args ->
        val count: Int = args.get("count") ?: 0
        val label: String = args.get("label") ?: ""
        val flags: Set<String> = args.get("flags") ?: emptySet()
        val option: String = args.get("option") ?: ""
        "label=$label count=$count flags=${flags.joinToString()} option=$option"
    }
}

internal data class SampleObject(
    val id: Int,
    val name: String,
    val flag: Boolean,
)

internal expect fun createSamplePlatformImage(): PlatformImage?
