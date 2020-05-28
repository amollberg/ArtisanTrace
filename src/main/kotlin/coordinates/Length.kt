package coordinates

import org.openrndr.math.Vector2
import org.openrndr.math.Vector3

data class Length(
    private var xy: Vector2,
    internal val system: System
) {
    fun xy() = xy

    fun three() =
        Vector3(xy().x, xy().y, 0.0)

    fun relativeTo(system: System) = system.get(this)

    fun plus(coordinate: Coordinate) = coordinate.plus(this)

    fun plus(other: Length): Length {
        val otherInThisSystem = other.relativeTo(system)
        return Length(xy() + otherInThisSystem.xy(), system)
    }
}
