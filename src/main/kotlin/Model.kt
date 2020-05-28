@file:UseSerializers(Vector2Serializer::class)

import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonException
import org.openrndr.draw.Drawer
import org.openrndr.draw.isolated
import org.openrndr.math.Vector2
import org.openrndr.math.transforms.transform
import java.io.File

val json = Json(JsonConfiguration.Stable.copy(prettyPrint = true))

@Serializable
class Model {
    var interfaces: MutableList<Interface> = mutableListOf()
    var traces: MutableList<Trace> = mutableListOf()
    var components: MutableList<Component> = mutableListOf()

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
            model.components.forEach {
                it.model = replaceComponentModel(it.model, model)
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
            val path = model.backingFile.toPath().toAbsolutePath().parent
                .resolve(componentModel.backingFile.toPath()).toFile()
            return loadFromFile(path) ?: throw SerializationException(
                "Component from file '$path' could not be loaded"
            )
        }
    }

    fun saveToFile() {
        interfaces.forEachIndexed { i, itf -> itf.id = i }
        components.forEach { it.model.saveToFile() }

        backingFile.writeText(serialize())
    }

    internal fun serialize(): String {
        return json.stringify(serializer(), this)
    }

    fun draw(drawer: Drawer, areInterfacesVisible: Boolean) {
        components.forEach { it.draw(drawer, areInterfacesVisible) }
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
}

@Serializable
class Component(
    @Serializable(with = ComponentModelPropertySerializer::class)
    var model: Model,
    var t: Transform
) {
    fun draw(drawer: Drawer, areInterfacesVisible: Boolean) {
        // drawer.isolated creates a receiver object which shadows the "this"
        // object
        val submodel = this
        drawer.isolated {
            t.apply(drawer)
            submodel.model.draw(drawer, areInterfacesVisible)
        }
    }
}

object ComponentModelPropertySerializer : KSerializer<Model> {
    override val descriptor: SerialDescriptor =
        PrimitiveDescriptor("ComponentModel", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Model {
        val backingFile = File(decoder.decodeString())
        val model = Model()
        model.backingFile = backingFile
        return model
    }

    override fun serialize(encoder: Encoder, value: Model) {
        encoder.encodeString(value.backingFile.path)
    }
}

@Serializable
class Transform(
    var scale: Double = 1.0,
    var rotation: Double = 0.0,
    var translation: Vector2 = Vector2.ZERO
) {
    fun apply(drawer: Drawer) {
        drawer.view *= transform {
            translate(translation)
            rotate(degrees = rotation)
            scale(scale)
        }
    }
}
