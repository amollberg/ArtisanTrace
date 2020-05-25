import org.openrndr.KeyModifier
import org.openrndr.MouseEvent
import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2
import kotlin.math.max

class InterfaceInsertTool(viewModel: ViewModel) : BaseTool(viewModel) {
    private var itf = interfaceLikeNearest(viewModel.mousePoint)

    override fun mouseClicked(position: Vector2) {
        val (trace, seg) = getNearestSegment(position) ?: return
        val newFirstSegment =
            TraceSegment(seg.getStart(), itf.getTerminals(), seg.angle)
        val newSecondSegment =
            TraceSegment(itf.getTerminals(), seg.getEnd(), seg.angle)
        // Replace the segment with the two new ones that go via the mouse
        trace.segments = trace.segments.flatMap {
            if (it == seg) listOf(newFirstSegment, newSecondSegment)
            else listOf(it)
        }.toMutableList()

        viewModel.interfaces.add(itf)
        itf = itf.clone()
    }

    // Copy-pasted from InterfaceDrawTool.mouseScrolled
    override fun mouseScrolled(mouse: MouseEvent) {
        if (mouse.modifiers.contains(KeyModifier.SHIFT)) {
            itf.length += 4 * mouse.rotation.y
        }
        else if (mouse.modifiers.contains(KeyModifier.ALT)) {
            itf.terminalCount += mouse.rotation.y.toInt()
            itf.terminalCount = max(1, itf.terminalCount)
        }
        else {
            itf.angle -= mouse.rotation.y * 45 % 360
        }
    }

    override fun draw(drawer: Drawer) {
        val position = viewModel.mousePoint
        val (trace, seg) = getNearestSegment(position) ?:
                           return
        itf.center = viewModel.mousePoint
        val newFirstSegment =
            TraceSegment(seg.getStart(), itf.getTerminals(), seg.angle)
        val newSecondSegment =
            TraceSegment(itf.getTerminals(), seg.getEnd(), seg.angle)

        newFirstSegment.draw(drawer)
        newSecondSegment.draw(drawer)
        itf.draw(drawer)
    }

    private fun getNearestSegment(position: Vector2):
            Pair<Trace, TraceSegment>? {
        return viewModel.traces.flatMap { trace ->
                trace.segments.map { segment ->
                    Triple(trace, segment, segment.getKnee())
                }
            }.minBy { (trace, segment, kneePosition) ->
                (kneePosition - position).length
            }?.let { (trace, segment, _) -> Pair(trace, segment) }
    }

    private fun interfaceLikeNearest(position: Vector2): Interface {
        val (trace, nearestSegment) = getNearestSegment(position) ?:
                                      return Interface(position, 0.0, 20.0, 1)
        return nearestSegment.getStart().hostInterface.clone()
    }
}
