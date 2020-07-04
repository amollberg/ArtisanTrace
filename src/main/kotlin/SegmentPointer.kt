import coordinates.System
import org.openrndr.shape.LineSegment
import org.openrndr.shape.Segment

data class SegmentPointer(val poly: Poly, val index: Int) {
    fun segment(system: System): Segment =
        Segment(start.xyIn(system), end.xyIn(system))

    fun lineSegment(system: System): LineSegment =
        LineSegment(start.xyIn(system), end.xyIn(system))

    val start get() = poly.points[index]
    val end get() = poly.points[Math.floorMod(index + 1, poly.points.size)]

    // TODO: Replace with Segment.equals after OPENRNDR issue #124 is resolved
    fun overlapsExactly(other: SegmentPointer) =
        poly.system!!.let { system ->
            listOf(start.relativeTo(system), end.relativeTo(system)) in
                    listOf(
                        other.start.relativeTo(system),
                        other.end.relativeTo(system)
                    ).let { listOf(it, it.reversed()) }
        }
}
