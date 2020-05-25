import org.openrndr.KeyModifier
import org.openrndr.MouseEvent
import org.openrndr.math.Vector2
import kotlin.math.max

open class BaseInterfaceTool(viewModel: ViewModel) : BaseTool(viewModel) {
    protected var itf = interfaceLikeNearest(viewModel.mousePoint)

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

    protected fun getNearestSegment(position: Vector2):
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
