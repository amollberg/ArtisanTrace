@file:UseSerializers(Vector2Serializer::class)

import DoubleOrRandom.Random
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

    val fileNameSuffix: String get() = dataClassToFileName(this)

    @Serializable
    data class RectGrid(
        var width: DoubleOrRandom = Random(20.0, 150.0),
        var height: DoubleOrRandom = Random(20.0, 150.0),
        var circleRadius: DoubleOrRandom = Random(1.0, 5.0),
        var countX: IntOrRandom = IntOrRandom.Random(2, 5),
        var countY: IntOrRandom = IntOrRandom.Random(2, 5),
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

    @Serializable
    data class MicroController(
        var pinsPerSide: Int = 8,
        var pinPitch: Double = 2.0,
        var pinLength: Double = 4.0,
        var pinCornerMargin: Double = 2 / 3.0
    ) : SvgMacro()

    @Serializable
    data class ZigZagEnd(
        var terminals: Int = 4
    ) : SvgMacro()

    @Serializable
    data class ViaArray(
        var terminals: Int = 2,
        var pitch: Double = 13.0
    ) : SvgMacro()
}
