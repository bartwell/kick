package ru.bartwell.kick.module.configuration

import ru.bartwell.kick.Kick
import ru.bartwell.kick.module.configuration.data.ConfigurationAccessor

public val Kick.Companion.configuration: ConfigurationAccessor
    get() = ConfigurationAccessor()
