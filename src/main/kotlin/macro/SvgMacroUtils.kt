import org.openrndr.shape.CompositionDrawer
import org.openrndr.shape.LineSegment

fun drawSvgInterface(
    drawer: CompositionDrawer,
    segment: LineSegment,
    terminalCount: Int
) {
    isolatedStyle(
        drawer,
        stroke = interfaceKeyColor(terminalCount)
    ) {
        drawer.lineSegment(segment)
    }
}
