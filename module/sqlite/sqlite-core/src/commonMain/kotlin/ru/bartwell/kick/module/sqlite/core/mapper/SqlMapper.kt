package ru.bartwell.kick.core.mapper

public interface SqlMapper<T> {
    public fun map(cursor: CursorWrapper<*>): T?
}
