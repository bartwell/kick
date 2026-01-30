package ru.bartwell.kick.module.firebase.cloudmessaging.feature.history.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.bartwell.kick.module.firebase.cloudmessaging.core.data.FirebaseMessage
import ru.bartwell.kick.module.firebase.cloudmessaging.core.persist.FirebaseCloudMessagingDatabase
import ru.bartwell.kick.module.firebase.cloudmessaging.core.persist.toDomain

internal class DefaultFirebaseCloudMessagingHistoryComponent(
    componentContext: ComponentContext,
    private val database: FirebaseCloudMessagingDatabase,
    private val onFinished: () -> Unit,
    private val onMessageClick: (FirebaseMessage) -> Unit,
) : FirebaseCloudMessagingHistoryComponent, ComponentContext by componentContext {

    private val uiScope = coroutineScope()
    private val _state = MutableValue(FirebaseCloudMessagingHistoryState())
    override val state: Value<FirebaseCloudMessagingHistoryState> = _state

    init {
        database.getMessageDao()
            .getAllAsFlow()
            .onEach { messages ->
                updateState { copy(messages = messages.map { it.toDomain() }) }
            }
            .launchIn(uiScope)
    }

    override fun onBackPressed() {
        onFinished()
    }

    override fun onClearMessages() {
        uiScope.launch {
            database.getMessageDao().deleteAll()
        }
    }

    override fun onMessageClick(message: FirebaseMessage) {
        onMessageClick.invoke(message)
    }

    private fun updateState(block: FirebaseCloudMessagingHistoryState.() -> FirebaseCloudMessagingHistoryState) {
        _state.value = _state.value.block()
    }
}
