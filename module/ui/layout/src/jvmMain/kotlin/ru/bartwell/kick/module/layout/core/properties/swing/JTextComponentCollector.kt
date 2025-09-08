package ru.bartwell.kick.module.layout.core.properties.swing

import ru.bartwell.kick.module.layout.core.common.PropertyNames
import ru.bartwell.kick.module.layout.core.common.add
import ru.bartwell.kick.module.layout.core.data.LayoutProperty
import ru.bartwell.kick.module.layout.core.properties.PropertyCollector
import java.awt.Component
import javax.swing.JTextArea
import javax.swing.JTextField
import javax.swing.text.JTextComponent

internal class JTextComponentCollector : PropertyCollector {
    override fun canCollect(c: Component) = c is JTextComponent
    override fun collect(c: Component): List<LayoutProperty> = buildList {
        val t = c as JTextComponent
        add(PropertyNames.TEXT_LENGTH, (t.document?.length ?: 0).toString())
        add(PropertyNames.EDITABLE, t.isEditable.toString())
        add(PropertyNames.CARET_POSITION, t.caretPosition.toString())
        add(PropertyNames.SELECTION, "${t.selectionStart}..${t.selectionEnd}")
        (t as? JTextArea)?.let { ta ->
            add(PropertyNames.ROWS, ta.rows.toString())
            add(PropertyNames.COLUMNS, ta.columns.toString())
            add(PropertyNames.LINE_WRAP, ta.lineWrap.toString())
        }
        (t as? JTextField)?.let { tf ->
            add(PropertyNames.COLUMNS, tf.columns.toString())
        }
    }
}
