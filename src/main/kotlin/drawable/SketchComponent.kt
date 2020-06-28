import coordinates.Coordinate
import coordinates.System
import coordinates.System.Companion.root
import kotlinx.serialization.*
import java.io.File

@Serializable
data class SketchComponent(
    @Serializable(with = SketchReferenceSerializer::class)
    var model: Model,
    override var system: System,
    override var groupId: Int = -1,
    override var groupOrdinal: Int = -1
) : Component, InterfaceComponent, GroupMember() {

    init {
        model.setReference(system)
    }

    override fun draw(drawer: OrientedDrawer) = model.draw(drawer, setOf())

    fun draw(drawer: OrientedDrawer, interfacesToIgnore: Set<Interface>) =
        model.draw(drawer, interfacesToIgnore)

    override val bounds: Poly
        get() {
            val points: List<Coordinate> = (
                    model.svgComponents.map {
                        it.bounds
                    } + model.sketchComponents.map {
                        it.bounds
                    } + model.interfaces.map {
                        it.bounds
                    } + model.traces.map {
                        it.bounds
                    }).flatMap { it.points }

            return convexHull(points)
        }

    override fun clone(parentModel: Model): Component {
        // Import a new instance of the model, placed on the same position
        val cloneComponent =
            parentModel.addSketch(model.backingFile, system.originCoord)!!
        // Make sure it has the same rotation and scaling as the original
        cloneComponent.system.setAbsoluteFrom(system)
        return cloneComponent
    }

    override val interfaces: List<Interface>
        get() = model.getInterfacesRecursively()
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
