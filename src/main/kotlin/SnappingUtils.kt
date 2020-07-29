import coordinates.Coordinate
import coordinates.Length

// Returns the Length to move the movingPoly so that it borders the poly
// while trying to approach the target coordinate
fun snappedTo(movingPoly: Poly, poly: Poly, target: Coordinate):
        Length {
    val originToTarget = target - movingPoly.system!!.originCoord
    val snapSegment = nearestSegment(poly, target)
        ?: return originToTarget
    val snapPoint =
        target.system.coord(
            snapSegment.lineSegment(target.system).nearest(target.xy())
        )

    val movingPoint = movingPoly.points.minBy { movingPoint ->
        countOverlappingPoints(movingPoly.moved(snapPoint - movingPoint), poly)
    } ?: return originToTarget

    return snapPoint - movingPoint
}

fun countOverlappingPoints(a: Poly, b: Poly) =
    a.points.filter { b.contains(it) }.count() +
            b.points.filter { a.contains(it) }.count()

fun nearestSegment(poly: Poly, point: Coordinate) =
    poly.segmentPointers.minBy {
        it.lineSegment(point.system).distance(point.xy())
    }

fun offsetOutwards(poly: Poly, distance: Double): Poly {
    return listOf(distance, -distance).map { offset ->
        poly.offsetBounds(offset)
    }.minBy { countOverlappingPoints(it, poly) }!!
}
