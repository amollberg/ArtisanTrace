import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MouseHoverTerminalSelectorTest : WithImplicitView() {

    @Test
    fun getTerminals() {
        var itf = Interface(at(x = 15.0), 0.0, 10.0, 4)
        view.model.interfaces = mutableListOf(itf)
        var selection = MouseHoverTerminalSelector(view)
        selection.desiredLeads = 3

        view.mousePoint = at(x = 0.0)
        assertEquals(
            Terminals(itf, 0..2),
            selection.getTerminals()
        )

        view.mousePoint = at(x = 15.5)
        assertEquals(
            Terminals(itf, 1..3),
            selection.getTerminals()
        )

        selection.desiredLeads = 1
        assertEquals(
            Terminals(itf, 2..2),
            selection.getTerminals()
        )
    }

    @Test
    fun rangeOfList() {
        assertEquals(0 until 10 step 1, toProgression(listOf(0, 1, 9, 7)))
        assertEquals(4 until 5 step 1, toProgression(listOf(4)))
        assertEquals(IntRange.EMPTY, toProgression(listOf()))
    }
}
