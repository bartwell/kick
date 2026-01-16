package ru.bartwell.kick.module.controlpanel.data

public sealed interface Editor {
    public data class List(val options: kotlin.collections.List<InputType>) : Editor
    public data class InputNumber(val min: Double?, val max: Double?) : Editor {
        public constructor() : this(null, null)

        public constructor(min: Double?) : this(min, null)
    }

    public data class InputString(val singleLine: Boolean) : Editor {
        public constructor() : this(true)
    }
}
