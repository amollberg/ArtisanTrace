import coordinates.System
import coordinates.System.Companion.root
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonException
import org.openrndr.svg.loadSVG
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
            return try {
                val model = json.parse(serializer(), string)
                model.backingFile = backingFile
                postProcessDeserialized(model)
            } catch (e: JsonException) {
                null
            } catch (e: SerializationException) {
                null
            }
        }

        private fun postProcessDeserialized(model: Model): Model {
            model.traces.forEach {
                it.segments.forEach {
                    it.start = replaceInterfaceUsingModel(it.start, model)
                    it.end = replaceInterfaceUsingModel(it.end, model)
                }
            }
            model.sketchComponents.forEach {
                it.model = replaceComponentModel(it.model, model)
                replaceComponentReferenceSystem(it, model)
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
        ): Terminals {
            val id = terminals.hostInterface.id
            return Terminals(model.interfaces.first {
                it.id == id
            }, terminals.range)
        }

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
            if (component.system.reference!!.reference == null) {
                component.system.reference = model.system
            }
        }
    }

    val components: List<Component> get() = sketchComponents + svgComponents

    fun saveToFile() {
        interfaces.forEachIndexed { i, itf -> itf.id = i }
        sketchComponents.forEach { it.model.saveToFile() }

        backingFile.writeText(serialize())
    }

    internal fun serialize(): String {
        return json.stringify(serializer(), this)
    }

    fun draw(drawer: OrientedDrawer, areInterfacesVisible: Boolean) {
        svgComponents.forEach { it.draw(drawer) }
        sketchComponents.forEach { it.draw(drawer, areInterfacesVisible) }
        traces.forEach { it.draw(drawer) }
        if (areInterfacesVisible) {
            interfaces
        } else {
            onlyUnconnectedInterfaces()
        }.forEach { it.draw(drawer) }
    }

    /** Return all interfaces that are not connected to a trace */
    private fun onlyUnconnectedInterfaces(): Set<Interface> {
        return interfaces.toSet() - traces.flatMap {
            it.segments.map {
                it.getStart().hostInterface
            }
        }.toSet() - traces.flatMap {
            it.segments.map {
                it.getEnd().hostInterface
            }
        }.toSet()
    }

    fun setReference(reference: System) {
        system.reference = reference
    }
}

