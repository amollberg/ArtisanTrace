package coordinates

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

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
}
