package ru.bartwell.kick.module.explorer.feature.viewer.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.module.explorer.feature.list.util.FileSystemUtils
import java.io.File

internal class DefaultFileViewerComponent(
    componentContext: ComponentContext,
    private val filePath: String,
    private val onFinished: () -> Unit,
) : FileViewerComponent, ComponentContext by componentContext {

    private val _model = MutableValue(FileViewerState())
    override val model: Value<FileViewerState> = _model

    init {
        loadData()
    }

    private fun loadData() {
        val text = FileSystemUtils.readFileText(filePath)
        val name = File(filePath).name
        _model.value = FileViewerState(fileName = name, text = text)
    }

    override fun onBackPressed() = onFinished()
}
