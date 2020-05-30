/** On hover, select a contiguous range of terminals near the mouse */
class MouseHoverTerminalSelector(private val viewModel: ViewModel) {
    var desiredLeads: Int = 1
    var reverseTerminalOrder = false

    fun getTerminals(): Terminals? {
        val itf = getInterface() ?: return null
        val nearestIndices = itf.getTerminals().range.sortedBy {
            (itf.getTerminalPosition(it) - viewModel.mousePoint)
                .lengthIn(viewModel.root)
        }.take(desiredLeads)
        return Terminals(
            itf, reversedIf(reverseTerminalOrder, toProgression(nearestIndices))
        )
    }

    fun draw(drawer: OrientedDrawer) {
        val terminals = getTerminals() ?: return
        val selectedInterface = terminals.hostInterface
        terminals.range.forEach { i ->
            drawer.drawer.circle(
                selectedInterface.getTerminalPosition(i).xyIn(drawer.system),
                6.0
            )
        }
    }

    fun getInterface(): Interface? {
        // Get the interface nearest to the mouse
        return getInterfacesRecursively(viewModel.model).minBy {
            (it.center - viewModel.mousePoint).lengthIn(viewModel.root)
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
