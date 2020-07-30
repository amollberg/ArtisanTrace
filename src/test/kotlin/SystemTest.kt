import TestUtils.Companion.dropFiles
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.openrndr.DropEvent
import org.openrndr.KeyModifier.SHIFT
import org.openrndr.math.Vector2
import java.io.File

class SystemTest : WithImplicitView() {
    @Test
    fun moveLoadedComponentWithTraces() {
        withTemporaryDirectory { directory ->
            val componentFile = createTempFile(directory = directory)
            val topFile = createTempFile(directory = directory)

            openFile(componentFile)
            view.changeTool(InterfaceDrawTool(view))
            clickMouse(at(10, 10))
            clickMouse(at(30, 40))
            assertEquals(2, view.model.interfaces.size)

            view.changeTool(TraceDrawTool(view))
            clickMouse(at(10, 10))
            clickMouse(at(30, 40))
            view.changeTool(EmptyTool(view))
            assertEquals(1, view.model.traces[0].segments.size)

            sendKey("s")

            openFile(topFile)
            dropFiles(
                DropEvent(Vector2(31.0, 22.0), listOf(componentFile)),
                setOf(SHIFT)
            )

            view.changeTool(ComponentMoveTool(view))
            clickMouse(at(31 + 20, 22 + 20))

            sendKey("s")
        }
    }

    private fun openFile(file: File) {
        dropFiles(view, DropEvent(Vector2.ZERO, listOf(file)))
        assertEquals(file, view.model.backingFile)
    }

    private fun withTemporaryDirectory(action: (directory: File) -> Unit) {
        val temporaryFileName = createTempFile().absoluteFile.path
        val temporaryDir = File(temporaryFileName + ".dir")
        temporaryDir.mkdir()
        assert(temporaryDir.isDirectory())
        try {
            action(temporaryDir)
        } finally {
            temporaryDir.deleteRecursively()
        }
    }
}
