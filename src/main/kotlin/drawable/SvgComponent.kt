import coordinates.System
import coordinates.System.Companion.transformFromTo
import kotlinx.serialization.*
import org.openrndr.math.Matrix33
import org.openrndr.math.Matrix44
import org.openrndr.shape.Composition
import org.openrndr.shape.ShapeNode
import org.openrndr.shape.map
import java.io.File
import kotlin.math.PI
import kotlin.math.atan2

@Serializable
class SvgComponent(
    @Serializable(with = SvgReferenceSerializer::class)
    var svg: Svg,
    override var system: System,
    var interfaces: MutableList<Interface> = mutableListOf()
) : Component {

    fun draw(drawer: OrientedDrawer) {
        drawer.drawer.root.children.add(transformed(drawer.system).root)
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

    fun inferInterfaces(model: Model) {
        svg.interfaceEnds.forEachIndexed { i, (start, end) ->
            val center = (start + end) * 0.5
            val line = end - start
            val angle = 180 / PI * atan2(line.y, line.x)
            if (i < interfaces.size) {
                // Modify the existing interface
                val itf = interfaces[i]
                itf.center = system.coord(center)
                itf.angle = angle
                itf.length = line.length
            } else {
                // Create a new interface
                val itf = Interface(
                    system.coord(center),
                    angle,
                    line.length,
                    2
                )
                interfaces.add(itf)
                model.interfaces.add(itf)
            }
        }
        svg.hideInterfaceShapes()
    }

    override fun clone(parentModel: Model): Component =
        // Import a new instance of the SVG, placed on the same position
        parentModel.addSvg(svg.backingFile, system.originCoord)
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
