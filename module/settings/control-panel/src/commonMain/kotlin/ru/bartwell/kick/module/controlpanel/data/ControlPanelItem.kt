package ru.bartwell.kick.module.controlpanel.data

public data class ControlPanelItem(
    val name: String,
    val type: ItemType,
    val editor: Editor?,
    val category: String?,
) {
    public constructor(name: String, type: ItemType) : this(
        name = name,
        type = type,
        editor = null,
        category = null,
    )

    public constructor(name: String, type: ItemType, category: String?) : this(
        name = name,
        type = type,
        editor = null,
        category = category,
    )

    public constructor(name: String, type: ItemType, editor: Editor?) : this(
        name = name,
        type = type,
        editor = editor,
        category = null,
    )
}
