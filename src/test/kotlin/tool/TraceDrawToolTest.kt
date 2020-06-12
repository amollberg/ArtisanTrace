import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TraceDrawToolTest : WithImplicitView() {
    @Test
    fun abortEmptyTrace() {
        var startItf = Interface(at(x = 10), 0.0, 10.0, 4)
        view.model.interfaces = mutableListOf(startItf)

        view.changeTool(TraceDrawTool(view))
        clickMouse(startItf.center)
        view.changeTool(EmptyTool(view))

        assertEquals(0, view.model.traces.size)
        assertEquals(1, view.model.interfaces.size)
    }
}
