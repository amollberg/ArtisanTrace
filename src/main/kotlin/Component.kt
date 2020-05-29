import coordinates.System
import org.openrndr.math.Vector2
import org.openrndr.shape.ShapeContour

interface Component {
    var system: System
    fun bounds(inSystem: System): ShapeContour

    fun origin(inSystem: System) = inSystem.get(system.coord(Vector2.ZERO))
}
