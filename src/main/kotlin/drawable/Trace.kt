import coordinates.System
import kotlinx.serialization.Serializable
import org.openrndr.color.ColorRGBa.Companion.GRAY
import org.openrndr.color.ColorRGBa.Companion.TRANSPARENT
import org.openrndr.math.Vector2
import kotlin.math.tan

@Serializable
data class Trace(
    var system: System,
    private var traceSegments: MutableList<TraceSegment> = mutableListOf(),
    override var groupId: Int = -1,
    override var groupOrdinal: Int = -1
) : GroupMember() {
    init {
        setSystem()
    }

    val segments: MutableList<TraceSegment> get() = traceSegments

    val terminals: List<Terminals>
        get() =
            if (traceSegments.isNotEmpty())
                traceSegments.map { it.start } + traceSegments.last().end
            else emptyList()

    override val bounds: Poly
        get() = segments.map { it.bounds }
            .fold(Poly(listOf()), { left, right ->
                Poly.join(left, right)!!
            })

    constructor(system: System, points: Iterable<TraceSegment>) :
            this(system, points.toMutableList())

    fun add(segment: TraceSegment) {
        if (traceSegments.isNotEmpty()) {
            assert(segment.getStart() == traceSegments.last().getEnd())
        }
        traceSegments.add(segment)
        setSystem()
    }

    fun replace(segment: TraceSegment, replacements: List<TraceSegment>) {
        traceSegments = traceSegments.flatMap {
            if (it == segment) replacements
            else listOf(it)
        }.toMutableList()
        setSystem()
    }

    override fun draw(drawer: OrientedDrawer) {
        traceSegments.forEach { it.draw(drawer) }
        isolatedStyle(
            drawer.drawer,
            stroke = GRAY,
            fill = TRANSPARENT
        ) {
            if (drawer.extendedVisualization)
                bounds.draw(drawer)
        }
    }

    fun withSegment(segment: TraceSegment) =
        Trace(system, traceSegments + segment)

    private fun setSystem() {
        traceSegments.forEach {
            it.system = system
        }
    }

    fun setCoordinateSystem(modelSystem: System) {
        system = modelSystem
        setSystem()
    }

    fun append(
        newTerminals: Terminals,
        angle: Angle = Angle.OBTUSE,
        reverseKnee: Boolean = false
    ) {
        if (traceSegments.isEmpty())
            throw IllegalStateException("Empty trace cannot be appended to.")
        add(
            TraceSegment(
                terminals.last(),
                newTerminals,
                angle, reverseKnee
            )
        )
    }
}

fun fold(v: Vector2, foldMatrix: Matrix22): Vector2 {
    return foldMatrix.times(v)
}

fun unfold(v: Vector2, foldMatrix: Matrix22): Vector2 {
    return foldMatrix.invert().times(v)
}

fun snapTo45(firstPoint: Vector2, secondPoint: Vector2): Vector2 {
    var vec = secondPoint - firstPoint
    var foldings = mutableListOf<Matrix22>()
    // Folding
    if (vec.x < 0) {
        foldings.add(Matrix22(-1, 0, 0, 1))
        vec = fold(vec, foldings.last())
    }
    if (vec.y < 0) {
        foldings.add(Matrix22(1, 0, 0, -1))
        vec = fold(vec, foldings.last())
    }
    if (vec.x < vec.y) {
        foldings.add(Matrix22(0, 1, 1, 0))
        vec = fold(vec, foldings.last())
    }

    // Snap inside 45 degree sector
    if (vec.x == 0.0) {
        // Do nothing
    } else if (vec.y / vec.x < tan(45 / 2.0 * Math.PI / 180)) {
        // Project to x-axis
        vec = Vector2(vec.x, 0.0)
    } else {
        // Project to diagonal
        val c = (vec.x + vec.y) / 2
        vec = Vector2(c, c)
    }

    // Undo folding
    foldings.reversed().forEach {
        vec = unfold(vec, it)
    }
    return firstPoint + vec
}

fun trace(system: System, f: TraceBuilder.() -> Unit): Trace {
    val builder = TraceBuilder(system)
    builder.f()
    return builder.result
}

class TraceBuilder(val system: System) {
    internal data class TerminalsWithConfig(
        val terminals: Terminals,
        val reverseKnee: Boolean
    )

    private val terminalsList: MutableList<TerminalsWithConfig> =
        mutableListOf()
    private var reverseKnee = false

    fun terminals(terminals: Terminals) {
        terminalsList.add(TerminalsWithConfig(terminals, reverseKnee))
    }

    fun reverseKnee(value: Boolean) {
        reverseKnee = value
    }

    val result: Trace
        get() = Trace(
            system,
            terminalsList.windowed(2) { (start: TerminalsWithConfig, end: TerminalsWithConfig) ->
                TraceSegment(
                    start.terminals,
                    end.terminals,
                    Angle.OBTUSE,
                    end.reverseKnee
                )
            }.toMutableList()
        )
}
