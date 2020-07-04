import coordinates.System.Companion.root
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.openrndr.math.Vector2

class SegmentPointerTest {
    companion object {
        val system = root()
    }

    @Test
    fun overlapsExactly() {
        val a = Poly(
            listOf(
                Vector2(0.0, 0.0),
                Vector2(30.0, 0.0),
                Vector2(100.0, 80.0)
            ).map { system.coord(it) }).segmentPointers
        val otherSystem = system.createSystem(Vector2(123.0, 456.0))
        val aRev = Poly(
            listOf(
                Vector2(100.0, 80.0),
                Vector2(30.0, 0.0),
                Vector2(0.0, 0.0)
            ).map { system.coord(it).relativeTo(otherSystem) }).segmentPointers
        assertTrue(a[0].overlapsExactly(aRev[1]))
        assertTrue(a[2].overlapsExactly(a[2]))
        assertFalse(a[0].overlapsExactly(a[1]))
    }
}
