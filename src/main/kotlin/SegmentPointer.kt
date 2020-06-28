import coordinates.System
import org.openrndr.shape.Segment

data class SegmentPointer(val poly: Poly, val index: Int) {
    fun segment(system: System): Segment = Segment(
        poly.points[index].xyIn(system),
        poly.points[Math.floorMod(index + 1, poly.points.size)].xyIn(system)
    )
}
