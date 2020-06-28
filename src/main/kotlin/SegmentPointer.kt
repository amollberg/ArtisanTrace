import org.openrndr.shape.Segment

data class SegmentPointer(val poly: Poly, val index: Int) {
    val segment: Segment
        get() = Segment(
            poly.points[index],
            poly.points[Math.floorMod(index + 1, poly.points.size)]
        )
}
