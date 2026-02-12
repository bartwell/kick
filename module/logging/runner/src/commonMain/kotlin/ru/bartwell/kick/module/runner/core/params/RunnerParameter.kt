package ru.bartwell.kick.module.runner.core.params

public data class RunnerParameter<T>(
    val id: String,
    val title: String,
    val description: String? = null,
    val type: RunnerParameterType<T>,
    val required: Boolean = false,
    val defaultValue: T? = null,
)

public sealed interface RunnerParameterType<T> {
    public object BooleanType : RunnerParameterType<Boolean>
    public data class IntType(val min: Int? = null, val max: Int? = null) : RunnerParameterType<Int>
    public data class LongType(val min: Long? = null, val max: Long? = null) : RunnerParameterType<Long>
    public data class FloatType(val min: Float? = null, val max: Float? = null) : RunnerParameterType<Float>
    public data class DoubleType(val min: Double? = null, val max: Double? = null) : RunnerParameterType<Double>
    public data class StringType(val multiline: Boolean = false) : RunnerParameterType<String>
    public data class SingleChoice<T : Any>(val options: List<T>) : RunnerParameterType<T>
    public data class MultiChoice<T : Any>(val options: List<T>) : RunnerParameterType<Set<T>>
}
