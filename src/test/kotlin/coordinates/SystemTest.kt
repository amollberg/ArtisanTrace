package coordinates

import TestUtils.Companion.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.openrndr.math.Vector2

class SystemTest {
    @Test
    fun derivesFrom() {
        val torso = System.root()
        val arm = torso.createSystem()
        val hand = arm.createSystem()
        val leg = torso.createSystem()

        assertTrue(arm.derivesFrom(torso))
        assertTrue(hand.derivesFrom(arm))
        assertTrue(hand.derivesFrom(torso))
        assertFalse(arm.derivesFrom(hand))
        assertFalse(torso.derivesFrom(hand))

        assertTrue(leg.derivesFrom(torso))
        assertFalse(torso.derivesFrom(leg))

        assertFalse(arm.derivesFrom(leg))
        assertFalse(hand.derivesFrom(leg))
        assertFalse(leg.derivesFrom(arm))
        assertFalse(leg.derivesFrom(hand))
    }

    @Test
    fun rotated() {
        val original = System.root().createSystem()
        val rotated = original.createRotated(90.0)

        val orig = original.coord(Vector2(0.0, 2.1))
        val rot = orig.relativeTo(rotated)
        assertEquals(
            Vector2(2.1, 0.0),
            rot.xy(),
            delta = 1e-15
        )
    }
}
