package ru.bartwell.kick.module.configuration.data

public data class ConfigurationItem(
    val name: String,
    val default: ValueType,
    val editor: Editor? = null,
)

