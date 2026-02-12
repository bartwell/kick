package ru.bartwell.kick.module.firebase.analytics.feature.main.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.bartwell.kick.module.firebase.analytics.core.persist.FirebaseAnalyticsDatabase
import ru.bartwell.kick.module.firebase.analytics.core.persist.FirebaseFloatingWindowSettings

internal class DefaultFirebaseAnalyticsComponent(
    componentContext: ComponentContext,
    private val database: FirebaseAnalyticsDatabase,
    private val onFinished: () -> Unit,
    private val onPropertiesClickCallback: () -> Unit,
) : FirebaseAnalyticsComponent, ComponentContext by componentContext {

    private val uiScope = coroutineScope()
    private val _model: MutableValue<FirebaseAnalyticsState> = MutableValue(FirebaseAnalyticsState())
    override val model: Value<FirebaseAnalyticsState> = _model

    init {
        subscribeEvents()
        subscribeProperties()
        subscribeUserId()
    }

    override fun onBackPressed() = onFinished()

    override fun onClearEvents() {
        uiScope.launch { database.getEventDao().deleteAll() }
    }

    override fun onPropertiesClick() = onPropertiesClickCallback()

    private fun subscribeEvents() {
        database.getEventDao()
            .getAllAsFlow()
            .onEach { updateState { copy(events = it, error = null) } }
            .catch { updateState { copy(error = it.toString()) } }
            .launchIn(uiScope)
    }

    private fun subscribeProperties() {
        database.getPropertyDao()
            .getAllAsFlow()
            .onEach { updateState { copy(properties = it, error = null) } }
            .catch { updateState { copy(error = it.toString()) } }
            .launchIn(uiScope)
    }

    private fun subscribeUserId() {
        FirebaseFloatingWindowSettings.observeUserId()
            .onEach { updateState { copy(userId = it) } }
            .launchIn(uiScope)
    }

    private fun updateState(block: FirebaseAnalyticsState.() -> FirebaseAnalyticsState) {
        _model.value = _model.value.block()
    }
}
