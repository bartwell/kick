package ru.bartwell.kick

public val shared: Kick = requireNotNull(Kick.instance, { "Kick is not initialized" })
