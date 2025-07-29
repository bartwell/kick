package ru.bartwell.kick.module.explorer.feature.view.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.explorer.feature.list.data.Result
import ru.bartwell.kick.module.explorer.feature.list.util.FileSystemUtils

internal class DefaultFileViewerComponent(
    componentContext: ComponentContext,
    private val path: String,
    private val onFinished: () -> Unit
) : FileViewerComponent, ComponentContext by componentContext {

    private val _model = MutableValue(FileViewerState())
    override val model: Value<FileViewerState> = _model

    override fun init(context: PlatformContext) {
        val result = FileSystemUtils.readText(path)
        when (result) {
            is Result.Success -> _model.value = model.value.copy(
                fileName = path.substringAfterLast('/'),
                text = result.data,
            )

            is Result.Error -> _model.value = model.value.copy(error = result.message)
        }
    }

    override fun onBackClick() = onFinished()

    override fun onErrorAlertDismiss() {
        _model.value = model.value.copy(error = null)
        onFinished()
    }
}
