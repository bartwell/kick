package ru.bartwell.kick.core.component

public interface Child<T : Component> {
    public val component: T
}
