import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class InterfaceTraceDrawToolTest : WithImplicitView() {

    @Test
    fun abortEmptyTrace() {
        var startItf = Interface(at(x = 10), 0.0, 10.0, 4)
        view.model.interfaces = mutableListOf(startItf)

        view.changeTool(InterfaceTraceDrawTool(view))
        clickMouse(startItf.center)
        view.changeTool(EmptyTool(view))

        assertEquals(0, view.model.traces.size)
        assertEquals(1, view.model.interfaces.size)
    }

    @Test
    fun correctNumberOfTerminals() {
        var startItf = Interface(at(x = 10), 0.0, 10.0, 4)
        view.model.interfaces = mutableListOf(startItf)
        val tool = InterfaceTraceDrawTool(view)
        view.changeTool(tool)
        tool.terminalSelector.desiredLeads = 3

        clickMouse(startItf.center)
        clickMouse(at(34, 56))
        val createdInterface = view.model.interfaces[1]
        assertEquals(4, startItf.terminalCount)
        assertEquals(3, createdInterface.terminalCount)
    }

    @Test
    fun sameTerminalDistance() {
        val originalCount = 4
        val cloneCount = 3

        var startItf = Interface(at(x = 10), 0.0, 10.0, originalCount)
        view.model.interfaces = mutableListOf(startItf)
        val tool = InterfaceTraceDrawTool(view)
        view.changeTool(tool)
        tool.terminalSelector.desiredLeads = cloneCount

        clickMouse(startItf.center)
        clickMouse(at(34, 56))
        val createdInterface = view.model.interfaces[1]

        assertEquals(
            terminalDistance(startItf),
            terminalDistance(createdInterface), 1e-10
        )
    }

    @Test
    fun projectOrthogonalOddNumberOfTerminals() {
        // Interface oriented vertically
        val itf = Interface(at(x = 20), 90.0, 40.0, 5)
        val terminals = Terminals(itf, 0..2)
        val inputPosition = at(17, 47)

        val expectedX = inputPosition.xyIn(view.root).x
        // The point should be on the same y-coordinate as terminal 1 which
        // is in the middle of 0..2
        val expectedY = terminals.hostInterface.getTerminalPosition(1)
            .xyIn(view.root).y
        assertEquals(
            at(expectedX, expectedY),
            projectOrthogonal(inputPosition, terminals)
        )
    }

    @Test
    fun projectOrthogonalEvenNumberOfTerminals() {
        // Interface oriented vertically
        val itf = Interface(at(x = 25), 90.0, 30.0, 4)
        val terminals = Terminals(itf, 0..3)
        val inputPosition = at(17, 47)

        val expectedX = inputPosition.xyIn(view.root).x
        // The point should be on the same y-coordinate as the middle of
        // terminal 1 and 2
        val expectedY = (terminals.hostInterface.getTerminalPosition(1)
            .xyIn(view.root).y + terminals.hostInterface.getTerminalPosition(2)
            .xyIn(view.root).y) / 2.0
        assertEquals(
            at(expectedX, expectedY),
            projectOrthogonal(inputPosition, terminals)
        )
    }

    fun terminalDistance(itf: Interface): Double {
        return when (itf.terminalCount) {
            1 -> itf.lengthIn(view.root)
            else -> (itf.getTerminalPosition(0) -
                    itf.getTerminalPosition(1)).lengthIn(view.root)
        }
    }
}
