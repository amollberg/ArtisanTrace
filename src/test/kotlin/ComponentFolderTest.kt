import TestUtils.Companion.dropFiles
import org.junit.jupiter.api.Test
import org.openrndr.DropEvent
import org.openrndr.math.Vector2
import java.io.File

class ComponentFolderTest : WithImplicitView() {
    @Test
    fun generateAndImportAllMacros() {
        File("components").walkTopDown().filter {
            it.extension == "atg"
        }.forEach { macroFile ->
            dropFiles(view, DropEvent(Vector2.ZERO, listOf(macroFile)))
        }
        // Then import all SVG files
        File("components").walkTopDown().filter {
            it.extension == "svg"
        }.forEach { svgFile ->
            dropFiles(view, DropEvent(Vector2.ZERO, listOf(svgFile)))
        }
    }
}
