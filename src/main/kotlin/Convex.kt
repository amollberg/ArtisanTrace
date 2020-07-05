import coordinates.Coordinate
import org.openrndr.math.Vector2

fun convexHull(polys: Iterable<Poly>) =
    convexHullOfCoordinates(polys.flatMap { it.points })

fun convexHull(poly: Poly) =
    convexHullOfCoordinates(poly.points)

fun convexHullOfCoordinates(coordinates: Iterable<Coordinate>) =
    Poly(
        pointsAsVector2 {
            convexHull(it.toTypedArray())
        }(coordinates.map { it.xyIn(coordinates.first().system) }).map {
            coordinates.first().system.coord(it)
        }
    )

fun pointsAsVector2(fnTakingPoints: (points: List<Point>) -> List<Point>) =
    { vectors: List<Vector2> ->
        fnTakingPoints(vectors.map { Point(it.x, it.y) })
            .map { Vector2(it.x, it.y) }
    }
