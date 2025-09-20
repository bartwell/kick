package ru.bartwell.kick.core.util

public interface WindowStateManager {
    public fun isWindowOpen(): Boolean
    public fun setWindowOpen()
    public fun setWindowClosed()

    public companion object {
        internal var instance: WindowStateManager? = null

        public fun init(manager: WindowStateManager) {
            this.instance = manager
        }

        public fun getInstance(): WindowStateManager? = instance
    }
}
