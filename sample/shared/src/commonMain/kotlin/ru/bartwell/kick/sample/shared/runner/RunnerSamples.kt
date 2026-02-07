package ru.bartwell.kick.sample.shared.runner

import ru.bartwell.kick.Kick
import ru.bartwell.kick.core.util.DateUtils
import ru.bartwell.kick.module.runner.RunnerModule
import ru.bartwell.kick.module.runner.core.data.PlatformImage
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
}

internal data class SampleObject(
    val id: Int,
    val name: String,
    val flag: Boolean,
)

internal expect fun createSamplePlatformImage(): PlatformImage?
