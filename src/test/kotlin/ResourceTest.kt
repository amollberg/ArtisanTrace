import TestUtils.Companion.assertListEquals
import TestUtils.Companion.dropFiles
import org.junit.jupiter.api.Assertions.assertEquals
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

    @Test
    fun ic1Infers2x6Interfaces() {
        dropFiles(
            view,
            DropEvent(
                Vector2.ZERO,
                listOf(File("src/test/resources/IC1.svg"))
            )
        )

        assertEquals(1, view.model.svgComponents.size)
        assertEquals(listOf(6, 6),
            view.model.svgComponents.first().interfaces.map { it.terminalCount })
    }

    private fun checkResource(path: String) {
        dropFiles(
            view,
            DropEvent(Vector2.ZERO, listOf(File(path)))
        )
        EXPECTED_INTERFACES[path]!!.zip(
            view.model.svgComponents.first().interfaces
        ).map { (expected, actual) ->
            checkInferredInterface(expected, actual, locationDelta = 1e-5)
        }
    }

    private fun checkInferredInterface(
        expected: InferredInterface,
        actual: Interface, locationDelta: Double = 0.0
    ) {
        assertListEquals(
            expected.ends,
            actual.getEnds().map { it.xy() },
            locationDelta
        )
        assertEquals(expected.terminalCount, actual.terminalCount)
    }
}
