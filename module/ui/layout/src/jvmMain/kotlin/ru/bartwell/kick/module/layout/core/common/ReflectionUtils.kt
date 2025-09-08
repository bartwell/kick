package ru.bartwell.kick.module.layout.core.common

import javax.swing.JComponent

internal object ReflectionUtils {
    private val jComponentClientPropsField by lazy {
        runCatching {
            JComponent::class.java.getDeclaredField(
                "clientProperties"
            ).apply { isAccessible = true }
        }.getOrNull()
    }
    private val arrayTableClass by lazy { runCatching { Class.forName("javax.swing.ArrayTable") }.getOrNull() }
    private val getKeysMethod by lazy {
        runCatching {
            arrayTableClass?.getDeclaredMethod("getKeys", Any::class.java)?.apply { isAccessible = true }
        }.getOrNull()
    }
    private val getMethod by lazy {
        runCatching {
            arrayTableClass?.getDeclaredMethod("get", Any::class.java, Any::class.java)?.apply {
                isAccessible = true
            }
        }.getOrNull()
    }

    fun readJClientProperties(jc: JComponent): Map<String, String?> {
        val table = runCatching { jComponentClientPropsField?.get(jc) }.getOrNull() ?: return emptyMap()
        val keys = runCatching { getKeysMethod?.invoke(null, table) as? Array<*> }.getOrNull() ?: return emptyMap()
        return buildMap {
            keys.forEach { k ->
                val v = runCatching { getMethod?.invoke(null, table, k) }.getOrNull()
                put(k.toString(), v?.toString())
            }
        }
    }
}
