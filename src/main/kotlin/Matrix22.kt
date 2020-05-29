import kotlinx.serialization.Serializable
import org.openrndr.math.Matrix33
import org.openrndr.math.Vector2
import kotlin.math.cos
import kotlin.math.sin

@Serializable
data class Matrix22(
    val c0r0: Double = 0.0, val c1r0: Double = 0.0,
    val c0r1: Double = 0.0, val c1r1: Double = 0.0
) {
    companion object {
        val IDENTITY = Matrix22(c0r0 = 1.0, c1r1 = 1.0)

        fun rotation(angleRadians: Double) = Matrix22(
            c0r0 = cos(angleRadians),
            c1r0 = -sin(angleRadians),
            c0r1 = sin(angleRadians),
            c1r1 = cos(angleRadians)
        )
    }

    constructor(
        c0r0: Int = 0, c1r0: Int = 0,
        c0r1: Int = 0, c1r1: Int = 0
    ) : this(
        c0r0.toDouble(), c1r0.toDouble(),
        c0r1.toDouble(), c1r1.toDouble()
    )

    constructor(m: Matrix33) : this(
        m.c0r0, m.c1r0,
        m.c0r1, m.c1r1
    )

    private val m
        get() = Matrix33(
            c0r0, c1r0, 0.0,
            c0r1, c1r1, 0.0,
            0.0, 0.0, 1.0
        )

    operator fun times(v: Vector2) = (m * v.xy0).xy

    operator fun times(o: Matrix22) = Matrix22(m * o.m)

    fun invert(): Matrix22 {
        val det = c0r0 * c1r1 - c0r1 * c1r0
        if (det == 0.0) {
            throw IllegalArgumentException(
                "Singular matrix $this cannot be inverted"
            )
        }
        val deti = 1 / det
        return Matrix22(
            deti * c1r1, -deti * c1r0,
            -deti * c0r1, deti * c0r0
        )
    }

    val columnMajor get() = listOf(listOf(c0r0, c0r1), listOf(c1r0, c1r1))

    override operator fun equals(other: Any?) =
        (other is Matrix22) && (columnMajor == other.columnMajor)

    operator fun times(d: Double) = Matrix22(m * d)
}
