import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.openrndr.math.Vector2

class InterfaceTraceDrawToolTest {

    @Test
    fun correctNumberOfTerminals() {
        var view = ViewModel()
        var startItf = Interface(Vector2(x = 10.0), 0.0, 10.0, 4)
        view.interfaces = mutableListOf(startItf)
        val tool = InterfaceTraceDrawTool(view)
        tool.terminalSelector.desiredLeads = 3

        tool.mouseClicked(startItf.center)
        tool.mouseClicked(Vector2(34.0, 56.0))
        val createdInterface = view.interfaces[1]
        assertEquals(4, startItf.terminalCount)
        assertEquals(3, createdInterface.terminalCount)
    }

    @Test
    fun sameTerminalDistance() {
        val originalCount = 4
        val cloneCount = 3

        var view = ViewModel()
        var startItf = Interface(Vector2(x = 10.0), 0.0, 10.0, originalCount)
        view.interfaces = mutableListOf(startItf)
        val tool = InterfaceTraceDrawTool(view)
        tool.terminalSelector.desiredLeads = cloneCount

        tool.mouseClicked(startItf.center)
        tool.mouseClicked(Vector2(34.0, 56.0))
        val createdInterface = view.interfaces[1]

        assertEquals(
            terminalDistance(startItf),
            terminalDistance(createdInterface), 1e-10)
    }

    fun terminalDistance(itf: Interface): Double {
        return when (itf.terminalCount) {
            1 -> itf.length
            else -> (itf.getTerminalPosition(0) -
                     itf.getTerminalPosition(1)).length
        }
    }
}
