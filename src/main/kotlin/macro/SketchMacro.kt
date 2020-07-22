import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration.Companion.Stable

@Serializable
sealed class SketchMacro {
    companion object {
        val json = Json(Stable.copy(prettyPrint = true))

        fun deserialize(string: String) = json.parse(serializer(), string)
    }

    fun serialize() = json.stringify(serializer(), this)

    @Serializable
    data class ObverseIcTrace(
        var pinsPerSide: Int = 3,
        var pinPitch: Double = 16.0,
        var width: Double = 18.0
    ) : SketchMacro()
}
