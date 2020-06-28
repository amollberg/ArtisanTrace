import coordinates.System.Companion.root
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.openrndr.math.Vector2

class GroupTest {
    @Test
    fun addMemberTwice() {
        val coordinateSystem = root()
        val member = Interface(
            coordinateSystem.coord(Vector2.ZERO), 0.0, 1.0, 1
        )
        val expectedGroup = Group()
        expectedGroup.add(member)

        val actualGroup = Group()
        actualGroup.add(member)
        actualGroup.add(member)
        assertEquals(expectedGroup, actualGroup)
    }
}
