package ru.bartwell.kick.module.sqlite.core.mapper

public interface SqlMapper<T> {
    public fun map(cursor: CursorWrapper<*>): T?
}
