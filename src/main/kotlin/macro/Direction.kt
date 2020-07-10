import java.lang.Math.floorMod

// Angle measured in multiples of 45 degrees counter-clockwise from positive
// x-axis in a system where y-axis points down
class Direction(startAngle45: Int) {
    private var value: Int = normalize(startAngle45)

    var angle45: Int
        get() = value
        set(newAngle45) {
            value = normalize(newAngle45)
        }

    operator fun plus(other: Int): Direction {
        return Direction(angle45 + other)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Direction
        if (value != other.value) return false
        return true
    }

    override fun hashCode(): Int {
        return value
    }

    private fun normalize(angle: Int) =
        floorMod(angle, 360 / 45)
}

operator fun Int.plus(direction: Direction): Direction =
    direction + this
