package ru.bartwell.kick.module.explorer.feature.options.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.explorer.feature.list.util.FileSystemUtils

internal class DefaultFileOptionsComponent(
    componentContext: ComponentContext,
    private val filePath: String,
    private val onViewFile: (String) -> Unit,
    private val onFinished: () -> Unit,
) : FileOptionsComponent, ComponentContext by componentContext {

    private val _model = MutableValue(FileOptionsState())
    override val model: Value<FileOptionsState> = _model

    override fun onDismiss() {
        onFinished()
    }

    override fun onViewAsText() {
        onFinished()
        onViewFile(filePath)
    }

    override fun onDownload(context: PlatformContext) {
        val exported = FileSystemUtils.exportFile(context, filePath)
        _model.value = FileOptionsState(
            isSheetVisible = false,
            alertMessage = exported?.let { "Saved to $it" }
        )
    }

    override fun onAlertDismiss() {
        onFinished()
    }
}
