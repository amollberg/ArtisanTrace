import TestUtils.Companion.dropFiles
import org.junit.jupiter.api.Test
import org.openrndr.DropEvent
import org.openrndr.math.Vector2
import java.io.File

class SketchMacroTest : WithImplicitView() {

    // Writes ATG files to components/ which will show up in the diff if changed
    @Test
    fun generateAllDefaults() {
        listOf(
            SketchMacro.ObverseIcTrace()
        ).forEach {
            val className = it::class.simpleName!!.toLowerCase()
            val macroFile = File("components/${className}_default.atg")
            macroFile.writeText(it.serialize())
            dropFiles(view, DropEvent(Vector2.ZERO, listOf(macroFile)))
        }
    }
}
