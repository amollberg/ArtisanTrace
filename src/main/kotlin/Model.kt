import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonException
import java.io.File

val json = Json(JsonConfiguration.Stable.copy(prettyPrint = true))

@Serializable
class Model {
    var interfaces: MutableList<Interface> = mutableListOf()
    var traces: MutableList<Trace> = mutableListOf()

    @Transient
    var backingFile = File("default.ats")

    companion object {
        fun loadFromFile(file: File): Model? {
            if (!file.isFile) {
                println("$file does not exist")
                return null
            }
            var model = deserialize(file.readText()) ?: return null
            model.backingFile = file
            return model
        }

        internal fun deserialize(string: String): Model? {
            return try {
                postProcessDeserialized(json.parse(serializer(), string))
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
    }

    fun saveToFile() {
        interfaces.forEachIndexed { i, itf -> itf.id = i }
        backingFile.writeText(serialize())
    }

    internal fun serialize(): String {
        return json.stringify(serializer(), this)
    }
}
