import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.openrndr.DropEvent
import org.openrndr.KeyModifier.SHIFT
import org.openrndr.math.Vector2
import java.io.File

class GroupAssignToolTest : WithImplicitView() {

    companion object {
        const val SKETCH_PATH = "src/test/resources/IC1.ats"
    }

    @Test
    fun addSketchComponentToGroup() {
        dropFiles(
            DropEvent(Vector2(30.0, 40.0), listOf(File(SKETCH_PATH))),
            setOf(SHIFT)
        )
        assertEquals(0, view.model.groups.size)

        view.changeTool(GroupAssignTool(view))
        clickMouse(
            at(Vector2(30.0, 40.0) + OFFSET_TO_BOUNDING_BOX[SKETCH_PATH]!!)
        )
        view.changeTool(EmptyTool(view))

        val expectedGroups = mutableListOf(
            Group(
                sketchComponents = mutableSetOf(view.model.sketchComponents.first())
            )
        )
        assertEquals(expectedGroups, view.model.groups)
    }
}
