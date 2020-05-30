import coordinates.System
import coordinates.System.Companion.transformFromTo
import kotlinx.serialization.*
import org.openrndr.math.Matrix33
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
    override var system: System
) : Component {

    fun draw(drawer: OrientedDrawer) {
        drawer.drawer.composition(transformed(drawer.system))
    }

    override fun bounds(inSystem: System) =
        transformed(inSystem).root.bounds.contour

    private fun transformed(toSystem: System) =
        Transformable(
            svg.composition!!,
            system,
            { c, m ->
                transformedSvg(toTranslatingMatrix44(m), c)
            }
        ).relativeTo(toSystem)
}

class Transformable<T>(
    private val obj: T,
    private val sourceSystem: System,
    private val transformAction: (obj: T, m: Matrix33) -> T
) {
    fun relativeTo(system: System) = transformAction(
        obj, transformFromTo(sourceSystem, system)
    )
}

fun transformedSvg(transform: Matrix44, composition: Composition) =
    Composition(composition.root.map {
        if (it is ShapeNode) {
            it.copy(shape = it.shape.transform(transform))
        } else {
            it
        }
    })

fun toTranslatingMatrix44(m: Matrix33) =
    with(m) {
        Matrix44(
            c0r0, c1r0, 0.0, c2r0,
            c0r1, c1r1, 0.0, c2r1,
            0.0, 0.0, 1.0, 0.0,
            c0r2, c1r2, 0.0, c2r2
        )
    }

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
