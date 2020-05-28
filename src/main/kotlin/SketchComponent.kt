import kotlinx.serialization.*
import org.openrndr.draw.Drawer
import org.openrndr.draw.isolated
import org.openrndr.shape.LineSegment
import org.openrndr.shape.ShapeContour
import java.io.File

@Serializable
class SketchComponent(
    @Serializable(with = SketchReferenceSerializer::class)
    var model: Model,
    override var transform: Transform
) : Component {
    fun draw(drawer: Drawer, areInterfacesVisible: Boolean) {
        // drawer.isolated creates a receiver object which shadows the "this"
        // object
        val sketchComponent = this
        drawer.isolated {
            transform.apply(drawer)
            sketchComponent.model.draw(drawer, areInterfacesVisible)
        }
    }

    override fun bounds(): ShapeContour {
        val contours = model.svgComponents.map {
            it.bounds()
        } + model.sketchComponents.map {
            it.bounds()
        } + model.interfaces.map {
            val (end1, end2) = it.getEnds()
            val line = LineSegment(end1, end2)
            line.contour
        } + model.traces.flatMap {
            it.segments.map {
                val knees = it.getKnees()
                val line = LineSegment(knees.first(), knees.last())
                line.contour
            }
        }
        return contours.reduce { acc, shapeContour ->
            acc.plus(shapeContour).bounds.shape.outline
        }.transform(transform.asMatrix())
    }
}

object SketchReferenceSerializer :
    KSerializer<Model> {
    override val descriptor: SerialDescriptor =
        PrimitiveDescriptor(
            "SketchComponent",
            PrimitiveKind.STRING
        )

    override fun deserialize(decoder: Decoder): Model {
        val backingFile =
            File(decoder.decodeString())
        val model = Model()
        model.backingFile = backingFile
        return model
    }

    override fun serialize(encoder: Encoder, value: Model) {
        encoder.encodeString(value.backingFile.path)
    }
}
