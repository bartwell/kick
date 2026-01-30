package ru.bartwell.kick.module.firebase.cloudmessaging.core.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import ru.bartwell.kick.module.firebase.cloudmessaging.core.data.FirebaseMessage
import ru.bartwell.kick.module.firebase.cloudmessaging.core.persist.toEntity

private const val MAX_MESSAGES: Long = 200

internal object FirebaseMessageLogger {
    private val loggerScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val messageFlow = MutableSharedFlow<FirebaseMessage>(
        replay = 0,
        extraBufferCapacity = 1_000,
        onBufferOverflow = BufferOverflow.SUSPEND,
    )

    init {
        loggerScope.launch {
            messageFlow.collect { message ->
                @Suppress("TooGenericExceptionCaught")
                try {
                    DatabaseHolder.database
                        ?.getMessageDao()
                        ?.let { dao ->
                            dao.insert(message.toEntity())
                            dao.trimToSize(MAX_MESSAGES)
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun log(message: FirebaseMessage) {
        loggerScope.launch {
            messageFlow.emit(message)
        }
    }
}
