import org.openrndr.math.Vector2

fun convexHull(poly: Poly) =
        Poly(Vector2.ZERO,
                pointsAsVector2 {
                    convexHull(it.toTypedArray())
                }(poly.points))

fun pointsAsVector2(fnTakingPoints: (points: List<Point>) -> List<Point>) =
        { vectors: List<Vector2> ->
            fnTakingPoints(vectors.map{ Point(it.x, it.y)})
                    .map { Vector2(it.x, it.y) }
        }
