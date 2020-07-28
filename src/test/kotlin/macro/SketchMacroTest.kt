import org.junit.jupiter.api.Test
import java.io.File

class SketchMacroTest {

    // Writes ATG files to components/ which will show up in the diff if changed
    @Test
    fun generateAllDefaults() {
        listOf(
            SketchMacro.ObverseIcTrace()
        ).forEach {
            val className = it::class.simpleName!!.toLowerCase()
            File("components/${className}_default.atg").writeText(
                it.serialize()
            )
        }
    }
}
