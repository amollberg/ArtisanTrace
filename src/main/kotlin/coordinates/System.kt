package coordinates

import Matrix22
import org.openrndr.math.Matrix33
import org.openrndr.math.Vector2
import org.openrndr.math.Vector3

data class System(
    private val reference: System?,
    /** In reference coordinate system */
    private var origin: Vector2 = Vector2.ZERO,
    private var axes: Matrix22 = Matrix22.IDENTITY
) {
    companion object {
        fun root() = System(null)
    }

    fun absoluteTransform(): Matrix33 {
        if (reference == null) {
            return Matrix33.IDENTITY
        }
        return reference.absoluteTransform() * transformToReference()
    }

    fun coord(xy: Vector2) = Coordinate(xy, this)

    fun length(xy: Vector2) =
        Length(xy, this)

    fun createSystem(origin: Vector2, axes: Matrix22 = Matrix22.IDENTITY) =
        System(this, origin, axes)

    fun get(coordinate: Coordinate) =
        Coordinate(internalGet(coordinate.three(), coordinate.system), this)

    fun get(length: Length) =
        Length(internalGet(length.three(), length.system), this)

    private fun internalGet(three: Vector3, system: System): Vector2 {
        if (system == this) {
            return three.xy
        }
        val sourceToAbs = system.absoluteTransform()
        val targetToAbs = absoluteTransform()
        val sourceToTarget = inversed(targetToAbs) * sourceToAbs
        val sourceThree = three
        val targetThree = sourceToTarget * sourceThree
        return targetThree.xy
    }

    fun setOrigin(newOrigin: Vector2) {
        origin = newOrigin
    }

    fun setAxes(newAxes: Matrix22) {
        axes = newAxes
    }

    private fun transformToReference() =
        Matrix33(
            axes.c0r0, axes.c1r0, origin.x,
            axes.c0r1, axes.c1r1, origin.y,
            0.0, 0.0, 1.0
        )
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
