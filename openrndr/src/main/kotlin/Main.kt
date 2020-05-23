import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2
import org.openrndr.shape.SegmentJoin.MITER
import org.openrndr.shape.contours

abstract class BaseTool(var viewModel : ViewModel) {

    open fun mouseClicked(position: Vector2) {
        // Empty
    }

    open fun draw(drawer : Drawer) {
        // Empty
    }

    open fun exit() {
        // Empty
    }
}

class Trace(var points: MutableList<Vector2> = mutableListOf<Vector2>()) {

    constructor(points: Iterable<Vector2>) : this(points.toMutableList())

    fun withPoint(point : Vector2): Trace {
        return Trace(points + point)
    }

    fun draw(drawer: Drawer) {
        val cs = contours {
            points.forEachIndexed { i, point ->
                if (i == 0) moveTo(point)
                else lineTo(point)
            }
        }
        if (cs.isNotEmpty()) {
            val c = cs.first()
            drawer.contour(c)
            drawer.contour(c.offset(15.0, joinType = MITER))
            drawer.contour(c.offset(-15.0, joinType = MITER))
        }
    }
}

class TraceDrawTool(viewModel: ViewModel) : BaseTool(viewModel) {
    private val trace = Trace()

    override fun mouseClicked(position : Vector2) {
        trace.points.add(position)
    }

    override fun draw(drawer : Drawer) {
        trace.withPoint(viewModel.mousePoint).draw(drawer)
    }

    override fun exit() {
        viewModel.traces.add(trace)
    }
}

class ViewModel {
    var mousePoint = Vector2(-1.0, -1.0)
    var traces : MutableList<Trace> = mutableListOf()
}

fun main() = application {
    configure {
        width = 768
        height = 576
    }

    program {
        var viewModel = ViewModel()
        val traceDrawTool = TraceDrawTool(viewModel)

        mouse.moved.listen {
            viewModel.mousePoint = it.position
        }
        mouse.clicked.listen {
            traceDrawTool.mouseClicked(it.position)
        }
        extend {
            drawer.fill = ColorRGBa.WHITE
            drawer.stroke = ColorRGBa.PINK
            drawer.strokeWeight = 2.0
            traceDrawTool.draw(drawer)
        }
    }
}