@file:UseSerializers(Vector2Serializer::class)
package coordinates

import Vector2Serializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.openrndr.math.Vector2
import org.openrndr.math.Vector3

@Serializable
data class Coordinate(
    private var xy: Vector2,
    internal val system: System
) {
    companion object {
        fun zeroIn(system: System) = system.coord(Vector2.ZERO)
    }

    fun xy() = xy

    fun xy(oriented: Oriented) = xyIn(oriented.system)

    fun xyIn(system: System) = relativeTo(system).xy()

    fun three() = Vector3(xy().x, xy().y, 1.0)

    fun relativeTo(system: System) = system.get(this)

    operator fun plus(length: Length): Coordinate {
        val otherInThisSystem = length.relativeTo(system)
        return Coordinate(xy() + otherInThisSystem.xy(), system)
    }

    operator fun minus(length: Length) = this + length * (-1.0)

    operator fun minus(coordinate: Coordinate) =
        Length(xy() - coordinate.relativeTo(system).xy(), system)

    fun set(position: Vector2) {
        xy = position
    }

    fun lerp(alpha: Double, coordinate: Coordinate) =
        Coordinate(xy() * alpha + coordinate.xyIn(system) * (1 - alpha), system)
}

interface Oriented {
    val system: System
}
