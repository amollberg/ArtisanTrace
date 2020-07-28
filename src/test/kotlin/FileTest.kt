import coordinates.System.Companion.root
import org.junit.jupiter.api.Test
import org.openrndr.DropEvent
import org.openrndr.KeyModifier.SHIFT
import org.openrndr.math.Vector2
import java.io.File

class FileTest {

    @Test
    fun insertSubdirSvg() {
        val view = ViewModel(Model(root()))
        TestUtils.dropFiles(
            view,
            DropEvent(Vector2.ZERO, listOf(File("src/test/resources/IC1.svg")))
        )
    }

    @Test
    fun insertSvgIntoSketchInSameSubdir() {
        val view =
            ViewModel(Model.loadFromFile(File("src/test/resources/IC1.ats"))!!)
        TestUtils.dropFiles(
            view,
            DropEvent(Vector2.ZERO, listOf(File("src/test/resources/IC1.svg")))
        )
    }

    @Test
    fun insertSketchIntoSketchInSameSubdir() {
        val view =
            ViewModel(Model.loadFromFile(File("src/test/resources/IC1.ats"))!!)
        TestUtils.dropFiles(
            view,
            DropEvent(Vector2.ZERO, listOf(File("src/test/resources/IC1.ats"))),
            setOf(SHIFT)
        )
    }

    @Test
    fun insertSubdirSketch() {
        val view = ViewModel(Model(root()))
        TestUtils.dropFiles(
            view,
            DropEvent(Vector2.ZERO, listOf(File("src/test/resources/IC1.ats"))),
            setOf(SHIFT)
        )
    }
}
