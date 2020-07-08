@file:UseSerializers(Vector2Serializer::class)
package coordinates

import Matrix22
import Vector2Serializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.openrndr.math.Matrix33
import org.openrndr.math.Vector2
import org.openrndr.math.Vector3

@Serializable
data class System(
    var reference: System?,
    /** In reference coordinate system */
    private var origin: Vector2 = Vector2.ZERO,
    var axes: Matrix22 = Matrix22.IDENTITY
) {
    companion object {
        fun root() = System(null)

        /** Return the transformation from source to target */
        fun transformFromTo(source: System, target: System): Matrix33 {
            val sourceToAbs = source.absoluteTransform()
            val targetToAbs = target.absoluteTransform()
            return inversed(targetToAbs) * sourceToAbs
        }
    }

    var originCoord
        get() = coord(Vector2.ZERO)
        set(coordinate: Coordinate) {
            origin = coordinate.xyIn(reference ?: this)
        }

    fun absoluteTransform(): Matrix33 {
        val ref = reference ?: return Matrix33.IDENTITY
        return ref.absoluteTransform() * transformToReference()
    }

    fun coord(xy: Vector2) = Coordinate(xy, this)

    fun length(xy: Vector2) =
        Length(xy, this)

    fun createSystem(
        origin: Vector2 = Vector2.ZERO, axes: Matrix22 = Matrix22.IDENTITY
    ) = System(this, origin, axes)

    fun get(coordinate: Coordinate) =
        Coordinate(internalGet(coordinate.three(), coordinate.system), this)

    fun get(length: Length) =
        Length(internalGet(length.three(), length.system), this)

    private fun internalGet(three: Vector3, system: System): Vector2 {
        if (system == this) {
            return three.xy
        }
        val sourceToTarget = transformFromTo(system, this)
        val targetThree = sourceToTarget * three
        return targetThree.xy
    }

    private fun transformToReference() =
        Matrix33(
            axes.c0r0, axes.c1r0, origin.x,
            axes.c0r1, axes.c1r1, origin.y,
            0.0, 0.0, 1.0
        )

    /** Sets the system to appear as the other system */
    fun setAbsoluteFrom(other: System) {
        val tf = transformFromTo(other, reference!!)
        origin = Vector2(tf.c2r0, tf.c2r1)
        axes = Matrix22(tf)
    }

    // True if changing other will change this system too
    fun derivesFrom(other: System): Boolean {
        if (this === other) return true
        return reference?.derivesFrom(other) ?: return false
    }
}

fun inversed(m: Matrix33): Matrix33 {
    val det = m.determinant
    val (a, d, g) = m[0]
    val (b, e, h) = m[1]
    val (c, f, i) = m[2]
    return Matrix33(
        e * i - f * h, c * h - b * i, b * f - c * e,
        f * g - d * i, a * i - c * g, c * d - a * f,
        d * h - e * g, b * g - a * h, a * e - b * d
    ) * (1 / det)
}
