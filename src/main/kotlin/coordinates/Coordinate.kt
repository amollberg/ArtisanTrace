package coordinates

import org.openrndr.math.Vector2
import org.openrndr.math.Vector3

data class Coordinate(
    private var xy: Vector2,
    internal val system: System
) {
    fun xy() = xy

    fun three() = Vector3(xy().x, xy().y, 1.0)

    fun relativeTo(system: System) = system.get(this)

    fun plus(length: Length): Coordinate {
        val otherInThisSystem = length.relativeTo(system)
        return Coordinate(xy() + otherInThisSystem.xy(), system)
    }
}
