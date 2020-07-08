import TestUtils.Companion.assertEquals
import TestUtils.Companion.dropFiles
import org.junit.jupiter.api.Test
import org.openrndr.DropEvent
import org.openrndr.math.Vector2
import java.io.File

class ResourceTest : WithImplicitView() {
    @Test
    fun ic1ExpectedInterfaceEnds() {
        checkResource("src/test/resources/IC1.svg")
    }

    @Test
    fun via2ExpectedInterfaceEnds() {
        checkResource("src/test/resources/Via2.svg")
    }

    @Test
    fun via3ExpectedInterfaceEnds() {
        checkResource("src/test/resources/Via3.svg")
    }

    private fun checkResource(path: String) {
        dropFiles(
            view,
            DropEvent(Vector2.ZERO, listOf(File(path)))
        )
        assertEquals(
            EXPECTED_INTERFACE_ENDS[path]!!,
            view.model.svgComponents.first().interfaces.map {
                it.getEnds().map { it.xyIn(view.root) }
            }, delta = 1e-5
        )
    }
}
