import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.openrndr.DropEvent
import org.openrndr.KeyModifier.SHIFT
import org.openrndr.math.Vector2
import java.io.File

class ComponentMoveToolTest : WithImplicitView() {

    companion object {
        val ORIGINAL_ORIGIN = Vector2(70.0, 200.0)
        val MOVED_ORIGIN = Vector2(200.0, 100.0)
    }

    @Test
    fun copySketchComponent() {
        dropCopyAndCheckPositions("src/test/resources/IC1.ats")
    }

    @Test
    fun copySvgComponent() {
        dropCopyAndCheckPositions("src/test/resources/IC1.svg")
    }

    private fun dropCopyAndCheckPositions(filePath: String) {
        dropFiles(
            DropEvent(
                ORIGINAL_ORIGIN,
                listOf(File(filePath))
            ), setOf(SHIFT)
        )

        view.changeTool(ComponentMoveTool(view))
        val boxOffset = OFFSET_TO_BOUNDING_BOX[filePath]!!
        moveMouse(at(ORIGINAL_ORIGIN + boxOffset))
        sendKey("c")
        clickMouse(at(MOVED_ORIGIN + boxOffset))

        assertEquals(2, view.model.components.size)
        assertEquals(
            ORIGINAL_ORIGIN,
            view.model.components[0].system.originCoord.xyIn(view.root)
        )
        assertEquals(
            MOVED_ORIGIN,
            view.model.components[1].system.originCoord.xyIn(view.root)
        )
    }
}
