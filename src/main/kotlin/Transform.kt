@file:UseSerializers(Vector2Serializer::class)

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2
import org.openrndr.math.transforms.transform

@Serializable
class Transform(
    var scale: Double = 1.0,
    var rotation: Double = 0.0,
    var translation: Vector2 = Vector2.ZERO
) {
    fun apply(drawer: Drawer) {
        drawer.view *= asMatrix()
    }

    fun asMatrix() = transform {
        translate(translation)
        rotate(degrees = rotation)
        scale(scale)
    }
}
