package ru.bartwell.kick.module.explorer.feature.list.data

internal sealed class Result {
    data class Success(val data: String) : Result()
    data class Error(val message: String) : Result()
}
