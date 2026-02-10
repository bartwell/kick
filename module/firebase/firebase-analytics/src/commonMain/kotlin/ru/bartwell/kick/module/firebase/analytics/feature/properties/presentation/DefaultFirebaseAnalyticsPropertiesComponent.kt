package ru.bartwell.kick.module.firebase.analytics.feature.properties.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.bartwell.kick.module.firebase.analytics.core.persist.FirebaseAnalyticsDatabase

internal class DefaultFirebaseAnalyticsPropertiesComponent(
    componentContext: ComponentContext,
    private val database: FirebaseAnalyticsDatabase,
    private val onFinished: () -> Unit,
) : FirebaseAnalyticsPropertiesComponent, ComponentContext by componentContext {

    private val uiScope = coroutineScope()
    private val _model: MutableValue<FirebaseAnalyticsPropertiesState> =
        MutableValue(FirebaseAnalyticsPropertiesState())
    override val model: Value<FirebaseAnalyticsPropertiesState> = _model

    init {
        subscribeProperties()
        subscribeUserId()
    }

    override fun onBackPressed() = onFinished()

    private fun subscribeProperties() {
        database.getPropertyDao()
            .getAllAsFlow()
            .onEach { updateState { copy(properties = it, error = null) } }
            .catch { updateState { copy(error = it.toString()) } }
            .launchIn(uiScope)
    }

    private fun subscribeUserId() {
        database.getUserIdDao()
            .getLatestAsFlow()
            .onEach { updateState { copy(userId = it?.value) } }
            .catch { updateState { copy(error = it.toString()) } }
            .launchIn(uiScope)
    }

    private fun updateState(block: FirebaseAnalyticsPropertiesState.() -> FirebaseAnalyticsPropertiesState) {
        _model.value = _model.value.block()
    }
}
