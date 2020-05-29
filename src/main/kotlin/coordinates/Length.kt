@file:UseSerializers(Vector2Serializer::class)
package coordinates

import Vector2Serializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.openrndr.math.Vector2
import org.openrndr.math.Vector3

@Serializable
data class Length(
    private var xy: Vector2,
    internal val system: System
) {
    fun xy() = xy

    fun xy(oriented: Oriented) = xyIn(oriented.system)

    fun xyIn(system: System) = relativeTo(system).xy()

    fun lengthIn(system: System) = xyIn(system).length

    fun three() = Vector3(xy().x, xy().y, 0.0)

    fun relativeTo(system: System) = system.get(this)

    fun plus(coordinate: Coordinate) = coordinate.plus(this)

    fun plus(other: Length): Length {
        val otherInThisSystem = other.relativeTo(system)
        return Length(xy() + otherInThisSystem.xy(), system)
    }

    operator fun times(d: Double) = Length(xy() * d, system)
}
