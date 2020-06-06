import coordinates.System
import coordinates.System.Companion.root
import kotlinx.serialization.*
import org.openrndr.shape.LineSegment
import org.openrndr.shape.ShapeContour
import java.io.File

@Serializable
class SketchComponent(
    @Serializable(with = SketchReferenceSerializer::class)
    var model: Model,
    override var system: System
) : Component {

    init {
        model.setReference(system)
    }

    fun draw(drawer: OrientedDrawer, areInterfacesVisible: Boolean) =
        model.draw(drawer, areInterfacesVisible)

    override fun bounds(inSystem: System): ShapeContour {
        val contours = model.svgComponents.map {
            it.bounds(inSystem)
        } + model.sketchComponents.map {
            it.bounds(inSystem)
        } + model.interfaces.map {
            val (end1, end2) = it.getEnds().map { it.xyIn(inSystem) }
            val line = LineSegment(end1, end2)
            line.contour
        } + model.traces.flatMap {
            it.segments.map {
                val knees = it.getKnees()
                val line =
                    LineSegment(
                        knees.first().xyIn(inSystem),
                        knees.last().xyIn(inSystem)
                    )
                line.contour
            }
        }
        return contours.reduce { acc, shapeContour ->
            acc.plus(shapeContour).bounds.shape.outline
        }
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
        val model = Model(System(root()))
        model.backingFile = backingFile
        return model
    }

    override fun serialize(encoder: Encoder, value: Model) {
        encoder.encodeString(value.backingFile.path)
    }
}
