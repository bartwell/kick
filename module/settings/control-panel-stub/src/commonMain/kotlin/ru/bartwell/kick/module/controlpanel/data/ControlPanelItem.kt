package ru.bartwell.kick.module.controlpanel.data

public data class ControlPanelItem(
    val name: String,
    val type: ItemType,
    val editor: Editor? = null,
    val category: String? = null,
)
