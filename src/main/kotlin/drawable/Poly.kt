import coordinates.Coordinate
import coordinates.System
import org.openrndr.math.Vector2
import org.openrndr.shape.ShapeContour
import org.openrndr.shape.compound
import org.openrndr.shape.contour

data class Poly(
    var points: List<Coordinate>
) {
    val system: System? get() = points.firstOrNull()?.system

    fun contour(system: System): ShapeContour = contour {
        points.forEach { moveOrLineTo(it.xyIn(system)) }
        close()
    }

    val segmentPointers: List<SegmentPointer>
        get() = points.indices.map { i ->
            SegmentPointer(this, i)
        }

    fun draw(drawer: OrientedDrawer) {
        drawer.drawer.contour(contour(drawer.system))
    }

    // Other points in the poly, same order, starting with the point after
    // the indicated one
    fun pointsAfter(point: Coordinate): List<Coordinate> {
        var pointRing = points
        if (!pointRing.contains(point))
            throw IllegalArgumentException("$point not in $pointRing")
        while (pointRing.first() != point) {
            pointRing = rotateFront(pointRing)
        }
        return pointRing.drop(1)
    }

    fun contains(point: Coordinate) =
        contour(point.system).contains(point.xy())

    val convexHull get() = convexHull(this)

    val concaveAreas
        get() = compound {
            difference {
                system?.ifPresent {
                    shape(convexHull.contour(it))
                    shape(contour(it))
                }
            }
        }

    companion object {
        fun rect(system: System, width: Int, height: Int) = Poly(
            listOf(
                Vector2(0.0, 0.0),
                Vector2(width.toDouble(), 0.0),
                Vector2(width.toDouble(), height.toDouble()),
                Vector2(0.0, height.toDouble())
            ).map { system.coord(it) })

        fun from(shapeContour: ShapeContour, system: System) =
            Poly(shapeContour
                .sampleLinear(0.5).segments.map {
                    system.coord(it.start)
                })

        // The first segment common between the Polys, if any.
        // Sensitive to winding direction.
        fun firstCommonSegment(a: Poly, b: Poly): SegmentPointer? =
            crossProduct(
                a.segmentPointers, b.segmentPointers
            ).firstOrNull { (aSeg, bSeg) ->
                aSeg.start == bSeg.start && aSeg.end == bSeg.end
            }?.first

    }
}
