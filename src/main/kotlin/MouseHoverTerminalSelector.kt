import org.openrndr.draw.Drawer

/** On hover, select a contiguous range of terminals near the mouse */
class MouseHoverTerminalSelector(private val viewModel: ViewModel) {
    var desiredLeads: Int = 1
    var reverseTerminalOrder = false

    fun getTerminals(): Terminals? {
        val itf = getInterface() ?: return null
        val nearestIndices = itf.getTerminals().range.sortedBy {
            (itf.getTerminalPosition(it) - viewModel.mousePoint).length
        }.take(desiredLeads)
        return Terminals(
            itf, reversedIf(
                reverseTerminalOrder,
                toProgression(nearestIndices)
            )
        )
    }

    fun draw(drawer: Drawer) {
        val terminals = getTerminals() ?: return
        val selectedInterface = terminals.hostInterface
        terminals.range.forEach { i ->
            drawer.circle(selectedInterface.getTerminalPosition(i), 6.0)
        }
    }

    fun getInterface(): Interface? {
        // Get the interface nearest to the mouse
        return viewModel.model.interfaces.minBy {
            (it.center - viewModel.mousePoint).length
        }
    }
}

fun toProgression(l: List<Int>): IntProgression {
    return if (l.isNotEmpty()) {
        l.min()!! until (l.max()!! + 1) step 1
    } else {
        IntRange.EMPTY
    }
}

fun reversedIf(condition: Boolean, progression: IntProgression) =
    if (condition) progression.reversed()
    else progression
