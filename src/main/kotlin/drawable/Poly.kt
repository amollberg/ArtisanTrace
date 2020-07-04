import coordinates.Coordinate
import coordinates.System
import coordinates.System.Companion.root
import org.openrndr.math.Vector2
import org.openrndr.shape.ShapeContour
import org.openrndr.shape.Triangle
import org.openrndr.shape.compound
import org.openrndr.shape.contour
import kotlin.math.abs

data class Poly(
    var points: List<Coordinate>
) {
    val system: System? get() = points.firstOrNull()?.system

    fun contour(system: System): ShapeContour = contour {
        points.forEach { moveOrLineTo(it.xyIn(system)) }
        close()
    }

    fun area(system: System): Double =
        contour(system).triangulation.sumByDouble { area(it) }

    val segmentPointers: List<SegmentPointer>
        get() = points.indices.map { i ->
            SegmentPointer(this, i)
        }

    val reversed get() = Poly(points.reversed())

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

    val segmentsOnConvexHull: Set<SegmentPointer>
        get() = segmentPointers.filter {
            segmentIsOnConvexHull(it, this)
        }.toSet()

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

        private fun segmentIsOnConvexHull(
            segmentPointer: SegmentPointer,
            poly: Poly
        ): Boolean = poly.convexHull.segmentPointers.any { hullPointer ->
            segmentPointer.overlapsExactly(hullPointer)
        }

        // The first segment common between the Polys, if any.
        // Sensitive to winding direction.
        fun firstCommonSegment(a: Poly, b: Poly): SegmentPointer? =
            crossProduct(
                a.segmentPointers, b.segmentPointers
            ).firstOrNull { (aSeg, bSeg) ->
                aSeg.start == bSeg.start && aSeg.end == bSeg.end
            }?.first

        fun nearestSegments(a: Poly, b: Poly, system: System) =
            crossProduct(a.segmentPointers, b.segmentPointers)
                .minBy { (aSeg, bSeg) ->
                    (aSeg.segment(system).position(0.5)
                            - bSeg.segment(system).position(0.5)).length
                }!!

        // If they share a segment, create a Poly with the remaining segments.
        // Note: Will not work as intended if a === b
        fun join(a: Poly, b: Poly): Poly? {
            if (a.points.isEmpty()) return b
            if (b.points.isEmpty()) return a

            val (other, commonSegment) = listOf(b, b.reversed).map {
                Pair(it, firstCommonSegment(a, it))
            }.firstOrNull { (_, commonSegment) ->
                commonSegment != null
            } ?: return null

            return Poly(
                a.pointsAfter(commonSegment!!.end) +
                        other.reversed.pointsAfter(commonSegment!!.start)
            )
        }

        // Make a bridge between the two disjoint polys and join them
        fun fuse(a: Poly, b: Poly): Poly {
            if (a.points.isEmpty()) return b
            if (b.points.isEmpty()) return a
            val (aSeg, bSeg) = Poly.nearestSegments(a, b, a.system!!)

            val bRev = if (aSeg.segment(root()).direction()
                    .dot(bSeg.segment(root()).direction()) < 0
            ) b
            else b.reversed

            val (_, bRevSeg) = Poly.nearestSegments(a, bRev, a.system!!)
            return Poly(
                emptyList<Coordinate>() +
                        aSeg.end +
                        a.pointsAfter(aSeg.end) +
                        bRevSeg.end +
                        bRev.pointsAfter(bRevSeg.end)

            )
        }
    }
}

fun area(tri: Triangle): Double {
    val (p1, p2, p3) = listOf(tri.x1, tri.x2, tri.x3)
    return abs(
        p1.x * p2.y + p2.x * p3.y + p3.x * p1.y
                - p1.y * p2.x - p2.y * p3.x - p3.y * p1.x
    ) / 2
}
