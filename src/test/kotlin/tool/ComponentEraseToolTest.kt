import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.openrndr.DropEvent
import org.openrndr.KEY_LEFT_SHIFT
import org.openrndr.math.Vector2
import java.io.File

class ComponentEraseToolTest : WithImplicitView() {

    companion object {
        const val SKETCH_PATH = "src/test/resources/IC1.ats"
        const val SVG_PATH = "src/test/resources/IC1.svg"
        val COMPONENT_ORIGIN = Vector2(50.0, 60.0)
    }

    @Test
    fun eraseSketchComponentWithConnectedTrace() {
        dropFiles(
            DropEvent(COMPONENT_ORIGIN, listOf(File(SKETCH_PATH))),
            setOf(KEY_LEFT_SHIFT)
        )
        assertEquals(1, view.model.sketchComponents.size)

        view.changeTool(InterfaceTraceDrawTool(view))
        clickMouse(
            at(COMPONENT_ORIGIN + OFFSET_TO_BOUNDING_BOX[SKETCH_PATH]!!)
        )
        clickMouse(at(50, 200))
        clickMouse(at(50, 400))
        view.changeTool(EmptyTool(view))
        assertEquals(1, view.model.traces.size)
        assertEquals(2, view.model.interfaces.size)

        view.changeTool(ComponentEraseTool(view))
        clickMouse(at(COMPONENT_ORIGIN + OFFSET_TO_BOUNDING_BOX[SKETCH_PATH]!!))
        assertEquals(0, view.model.sketchComponents.size)
    }

    @Test
    fun eraseSvgComponentWithConnectedTrace() {
        dropFiles(DropEvent(COMPONENT_ORIGIN, listOf(File(SVG_PATH))))
        assertEquals(1, view.model.svgComponents.size)

        view.changeTool(InterfaceTraceDrawTool(view))
        clickMouse(
            at(COMPONENT_ORIGIN + OFFSET_TO_BOUNDING_BOX[SVG_PATH]!!)
        )
        clickMouse(at(50, 200))
        clickMouse(at(50, 400))
        view.changeTool(EmptyTool(view))
        assertEquals(1, view.model.traces.size)
        assertEquals(2, view.model.svgInterfaces.size)
        assertEquals(2, view.model.interfaces.size)

        view.changeTool(ComponentEraseTool(view))
        clickMouse(at(COMPONENT_ORIGIN + OFFSET_TO_BOUNDING_BOX[SVG_PATH]!!))
        assertEquals(0, view.model.svgComponents.size)
        assertEquals(0, view.model.svgInterfaces.size)
        assertEquals(2, view.model.interfaces.size)
    }
}
