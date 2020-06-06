import coordinates.System
import coordinates.System.Companion.root
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.openrndr.shape.CompositionDrawer
import org.openrndr.svg.loadSVG
import org.openrndr.svg.writeSVG
import java.io.File

val json = Json(JsonConfiguration.Stable.copy(prettyPrint = true))

@Serializable
class Model(@Transient val system: System = root()) {
    var interfaces: MutableList<Interface> = mutableListOf()
    var traces: MutableList<Trace> = mutableListOf()
    var sketchComponents: MutableList<SketchComponent> = mutableListOf()
    var svgComponents: MutableList<SvgComponent> = mutableListOf()

    @Transient
    var backingFile = File("default.ats")

    companion object {
        fun loadFromFile(file: File): Model? {
            if (!file.isFile) {
                println("$file does not exist")
                return null
            }
            var model = deserialize(file.readText(), file) ?: return null
            return model
        }

        internal fun deserialize(string: String, backingFile: File): Model? {
            val model = json.parse(serializer(), string)
            model.backingFile = backingFile
            return postProcessDeserialized(model)
        }

        private fun postProcessDeserialized(model: Model): Model {
            // Note: Sketch components must be loaded before traces in order
            // to have all interfaces in components loaded before being
            // dereferenced by the trace terminals
            model.sketchComponents.forEach {
                it.model = replaceComponentModel(it.model, model)
                // Connect the model system with the component system
                it.model.setReference(it.system)
                // Connect the component system to the top-level model root
                replaceComponentReferenceSystem(it, model)
            }
            // Re-index interfaces to the combined model
            model.getInterfacesRecursively().forEachIndexed { i, itf ->
                itf.id = i
            }
            model.traces.forEach { trace ->
                trace.segments.forEach {
                    it.start = replaceInterfaceUsingModel(it.start, model)
                    it.end = replaceInterfaceUsingModel(it.end, model)
                }
            }
            model.interfaces.forEach {
                it.center = model.system.coord(it.center.xy())
            }
            model.svgComponents.forEach {
                it.svg = loadFromBackingFile(it.svg)
                replaceComponentReferenceSystem(it, model)
            }
            return model
        }

        /** Use the ID to replace the interface with the correct instance
         *  from the view model interface list.
         */
        private fun replaceInterfaceUsingModel(
            terminals: Terminals,
            model: Model
        ) = Terminals(
            model.getInterfacesRecursively()
                .first { it.id == terminals.hostInterface.id },
            terminals.range
        )

        /** Use the backingFile to load the model with the correct content */
        private fun replaceComponentModel(
            componentModel: Model,
            model: Model
        ): Model {
            val workingDir = model.backingFile.toPath().toAbsolutePath().parent
            val path =
                workingDir.resolve(componentModel.backingFile.toPath()).toFile()
            return loadFromFile(path) ?: throw SerializationException(
                "Sketch component from file '$path' could not be loaded"
            )
        }

        private fun loadFromBackingFile(componentSvg: Svg): Svg {
            return Svg(
                loadSVG(componentSvg.backingFile.path),
                componentSvg.backingFile
            )
        }

        private fun replaceComponentReferenceSystem(
            component: Component,
            model: Model
        ) {
            component.system.reference?.let { assertIsRootSystem(it) }
            component.system.reference = model.system
        }
    }

    val components: List<Component> get() = sketchComponents + svgComponents

    fun saveToFile() {
        backingFile.writeText(serialize())
    }

    fun exportToSvg() {
        val cd = CompositionDrawer()
        draw(
            OrientedDrawer(cd, system),
            interfacesToIgnore = getInterfacesRecursively().toSet()
        )
        svgFile.writeText(writeSVG(cd.composition))
    }

    private val svgFile: File get() = File(backingFile.path + ".svg")

    internal fun serialize(): String {
        getInterfacesRecursively().forEachIndexed { i, itf -> itf.id = i }
        return json.stringify(serializer(), this)
    }

    fun draw(
        drawer: OrientedDrawer,
        interfacesToIgnore: Set<Interface>
    ) {
        svgComponents.forEach { it.draw(drawer) }
        sketchComponents.forEach {
            it.draw(drawer, interfacesToIgnore)
        }
        traces.forEach { it.draw(drawer) }
        (interfaces - interfacesToIgnore).forEach { it.draw(drawer) }
    }

    /** Return all interfaces that are connected to a trace */
    fun connectedInterfaces(): Set<Interface> =
        getTracesRecursively().flatMap {
            it.segments.map {
                setOf(
                    it.getStart().hostInterface,
                    it.getEnd().hostInterface
                )
            }
        }.fold(setOf()) { acc, itfSet -> acc.union(itfSet) }

    fun setReference(reference: System) {
        system.reference = reference
    }

    fun getInterfacesRecursively(): List<Interface> =
        interfaces + sketchComponents.flatMap {
            it.model.getInterfacesRecursively()
        }

    fun getTracesRecursively(): List<Trace> =
        traces + sketchComponents.flatMap {
            it.model.getTracesRecursively()
        }
}

fun assertIsRootSystem(system: System) {
    if (system.reference != null) throw IllegalArgumentException(
        "System ${system} should have reference 'null'"
    )
}
