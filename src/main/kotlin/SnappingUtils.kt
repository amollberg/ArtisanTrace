import Poly.Companion.overlap
import coordinates.Coordinate
import coordinates.Length

// Returns the Length to move the movingPoly so that it borders the poly
// while trying to approach the target coordinate
fun snappedTo(movingPoly: Poly, poly: Poly, target: Coordinate): Length {
    val originToTarget = target - movingPoly.system!!.originCoord
    val snapSegment = nearestSegment(poly, target)
        ?: return originToTarget
    val pointOnBound =
        target.system.coord(
            snapSegment.lineSegment(target.system).nearest(target.xy())
        )

    val movingPoint = movingPoly.points.minBy { movingPoint ->
        measureOverlappingArea(
            movingPoly.moved(pointOnBound - movingPoint),
            poly
        )
    } ?: return originToTarget

    return pointOnBound - movingPoint
}

fun measureOverlappingArea(a: Poly, b: Poly) =
    overlap(a, b).sumByDouble { it.area }

fun nearestSegment(poly: Poly, point: Coordinate) =
    poly.segmentPointers.minBy {
        it.lineSegment(point.system).distance(point.xy())
    }

fun offsetOutwards(poly: Poly, distance: Double): Poly {
    return listOf(distance, -distance).map { offset ->
        poly.offsetBounds(offset)
    }.maxBy { it.area }!!
}
