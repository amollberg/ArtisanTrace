@file:UseSerializers(Vector2Serializer::class)

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

@Serializable
sealed class SvgMacro {
    companion object {
        val json = Json(JsonConfiguration.Stable.copy(prettyPrint = true))

        fun deserialize(string: String) = json.parse(serializer(), string)
    }

    fun serialize() = json.stringify(serializer(), this)

    @Serializable
    data class RectGrid(
        var width: Double = 100.0,
        var height: Double = 100.0,
        var circleRadius: Double = 2.0,
        var countX: Int = 2,
        var countY: Int = 2,
        var margin: Double = 0.05
    ) : SvgMacro()

    @Serializable
    data class VerticalPins(
        var pins: Int = 2,
        var pinSize: Double = 6.0,
        var margin: Double = 1.2
    ) : SvgMacro()

    @Serializable
    data class IntegratedCircuit(
        var pinsPerSide: Int = 3,
        var pinPitch: Double = 8.0,
        var pinLength: Double = 4.0,
        var pinCornerMargin: Double = 2 / 3.0,
        var width: Double = 11.0
    ) : SvgMacro()
}
