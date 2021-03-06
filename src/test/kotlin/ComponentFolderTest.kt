import TestUtils.Companion.dropFiles
import org.junit.BeforeClass
import org.junit.jupiter.api.Test
import org.openrndr.DropEvent
import org.openrndr.math.Vector2
import java.io.File

class ComponentFolderTest : WithImplicitView() {
    @BeforeClass
    fun cleanBuildDir() {
        File("build/components").deleteRecursively()
    }

    @Test
    fun generateAndImportAllMacros() {
        // Copy all files to build directory
        File("components").walkTopDown().filter {
            it.extension == "atg"
        }.forEach { macroFile ->
            val targetFile = File("build/$macroFile")
            targetFile.mkdirs()
            macroFile.copyTo(targetFile, true)
        }
        // Then import all macro files
        File("build/components").walkTopDown().filter {
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
