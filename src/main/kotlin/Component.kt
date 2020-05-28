import org.openrndr.shape.ShapeContour

interface Component {
    var transform: Transform
    fun bounds(): ShapeContour

    fun origin() = transform.translation
}
