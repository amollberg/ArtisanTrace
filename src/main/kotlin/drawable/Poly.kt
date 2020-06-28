import org.openrndr.draw.Drawer
import org.openrndr.extra.noise.random
import org.openrndr.math.Vector2
import org.openrndr.shape.ShapeContour
import org.openrndr.shape.compound
import org.openrndr.shape.contour
import kotlin.math.PI

data class Poly(
    var origin: Vector2,
    private var relativePoints: List<Vector2>
) {
    val contour: ShapeContour
        get() = contour {
            points.forEach { moveOrLineTo(it) }
            close()
        }

    var points
        get() = relativePoints.map { origin + it }
        set(value) {
            relativePoints = value.map { it - origin }
        }

    val segmentPointers: List<SegmentPointer>
        get() = (0..(points.size - 2)).map { i ->
            SegmentPointer(this, i)
        }

    fun draw(drawer: Drawer) {
        drawer.contour(contour)
    }

    fun movedTo(newOrigin: Vector2): Poly {
        origin = newOrigin
        return this
    }

    fun rotated(radiansCcw: Double): Poly =
        Poly(origin, relativePoints.map {
            Matrix22.rotation(radiansCcw).times(it)
        })

    // Other points in the poly, same order, starting with the point after
    // the indicated one
    fun pointsAfter(point: Vector2): List<Vector2> {
        var pointRing = points
        if (!pointRing.contains(point))
            throw IllegalArgumentException("$point not in $pointRing")
        while (pointRing.first() != point) {
            pointRing = rotateFront(pointRing)
        }
        return pointRing.drop(1)
    }

    val convexHull get() = convexHull(this)

    val concaveAreas
        get() = compound {
            difference {
                shape(convexHull.contour)
                shape(contour)
            }
        }

    companion object {
        fun rect(width: Int, height: Int, rotation: Int) = Poly(
            Vector2.ZERO,
            listOf(
                Vector2(0.0, 0.0),
                Vector2(width.toDouble(), 0.0),
                Vector2(width.toDouble(), height.toDouble()),
                Vector2(0.0, height.toDouble())
            ).map { Matrix22.rotation(rotation * 45 * PI / 180).times(it) })

        fun randomRect() =
            rect(
                width = random(10.0, 100.0).toInt(),
                height = random(10.0, 100.0).toInt(),
                rotation = random(0.0, 8.0).toInt()
            )

        fun rightTriangle(firstCathenus: Int, secondCathenus: Int) = Poly(
            Vector2.ZERO,
            listOf(
                Vector2(0.0, 0.0),
                Vector2(firstCathenus.toDouble(), 0.0),
                Vector2(0.0, secondCathenus.toDouble())
            )
        )

        fun randomRightTriangle() =
            rightTriangle(
                firstCathenus = random(10.0, 100.0).toInt(),
                secondCathenus = random(10.0, 100.0).toInt()
            )

        fun randomShape() =
            when (random(0.0, 1.999).toInt()) {
                0 -> randomRect()
                else -> randomRightTriangle()
            }
    }
}
