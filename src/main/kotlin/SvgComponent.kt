import kotlinx.serialization.*
import org.openrndr.draw.Drawer
import org.openrndr.math.Matrix44
import org.openrndr.shape.Composition
import org.openrndr.shape.ShapeNode
import org.openrndr.shape.map
import java.io.File

data class Svg(var composition: Composition? = null, val backingFile: File)

@Serializable
class SvgComponent(
    @Serializable(with = SvgReferenceSerializer::class)
    var svg: Svg,
    override var transform: Transform
) : Component {
    fun draw(drawer: Drawer) {
        drawer.composition(
            transformedSvg(
                transform.asMatrix(),
                svg.composition!!
            )
        )
    }

    override fun bounds() =
        svg.composition!!.root.bounds.contour.transform(transform.asMatrix())
}

fun transformedSvg(transform: Matrix44, composition: Composition) =
    Composition(composition.root.map {
        if (it is ShapeNode) {
            it.copy(shape = it.shape.transform(transform))
        } else {
            it
        }
    })

class SvgReferenceSerializer :
    KSerializer<Svg> {
    override val descriptor: SerialDescriptor =
        PrimitiveDescriptor(
            "SvgComponent",
            PrimitiveKind.STRING
        )

    override fun deserialize(decoder: Decoder): Svg {
        val backingFile =
            File(decoder.decodeString())
        return Svg(null, backingFile)
    }

    override fun serialize(encoder: Encoder, value: Svg) {
        encoder.encodeString(value.backingFile.path)
    }
}
