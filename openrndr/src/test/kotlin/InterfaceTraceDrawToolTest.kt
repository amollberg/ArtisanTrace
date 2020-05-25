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

    @Test
    fun projectOrthogonalOddNumberOfTerminals() {
        // Interface oriented vertically
        val itf = Interface(Vector2(x = 20.0), 90.0, 40.0, 5)
        val terminals = Terminals(itf, 0..2)
        val inputPosition = Vector2(17.0, 47.0)

        val expectedX = inputPosition.x
        // The point should be on the same y-coordinate as terminal 1 which
        // is in the middle of 0..2
        val expectedY = terminals.hostInterface.getTerminalPosition(1).y
        assertEquals(
            Vector2(expectedX, expectedY),
            projectOrthogonal(inputPosition, terminals))
    }

    @Test
    fun projectOrthogonalEvenNumberOfTerminals() {
        // Interface oriented vertically
        val itf = Interface(Vector2(x = 25.0), 90.0, 30.0, 4)
        val terminals = Terminals(itf, 0..3)
        val inputPosition = Vector2(17.0, 47.0)

        val expectedX = inputPosition.x
        // The point should be on the same y-coordinate as the middle of
        // terminal 1 and 2
        val expectedY =
            (terminals.hostInterface.getTerminalPosition(1).y +
             terminals.hostInterface.getTerminalPosition(2).y) / 2.0
        assertEquals(
            Vector2(expectedX, expectedY),
            projectOrthogonal(inputPosition, terminals))
    }

    fun terminalDistance(itf: Interface): Double {
        return when (itf.terminalCount) {
            1 -> itf.length
            else -> (itf.getTerminalPosition(0) -
                     itf.getTerminalPosition(1)).length
        }
    }
}
