import coordinates.System
import org.openrndr.shape.Segment

data class SegmentPointer(val poly: Poly, val index: Int) {
    fun segment(system: System): Segment =
        Segment(start.xyIn(system), end.xyIn(system))

    val start get() = poly.points[index]
    val end get() = poly.points[Math.floorMod(index + 1, poly.points.size)]
}
