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
        var countX: IntOrRandom = IntOrRandom.Random(2, 4),
        var countY: IntOrRandom = IntOrRandom.Random(2, 4),
        var margin: Double = 0.05
    ) : SvgMacro()

    @Serializable
    data class VerticalPins(
        var pins: IntOrRandom = IntOrRandom.Random(1, 6),
        var pinSize: DoubleOrRandom = Random(3.0, 9.0),
        var margin: Double = 1.2
    ) : SvgMacro()

    @Serializable
    data class IntegratedCircuit(
        var pinsPerSide: IntOrRandom = IntOrRandom.Random(2, 6),
        var pinPitch: DoubleOrRandom = Random(4.0, 12.0),
        var pinLength: Double = 4.0,
        var pinCornerMargin: Double = 2 / 3.0,
        var width: DoubleOrRandom = Random(8.0, 30.0)
    ) : SvgMacro()

    @Serializable
    data class MicroController(
        var pinsPerSide: IntOrRandom = IntOrRandom.Random(2, 12),
        var pinPitch: DoubleOrRandom = Random(1.5, 4.0),
        var pinLength: Double = 4.0,
        var pinCornerMargin: Double = 2 / 3.0
    ) : SvgMacro()

    @Serializable
    data class ZigZagEnd(
        var terminals: IntOrRandom = IntOrRandom.Random(2, 10)
    ) : SvgMacro()

    @Serializable
    data class ViaArray(
        var terminals: IntOrRandom = IntOrRandom.Random(2, 11),
        var pitch: DoubleOrRandom = Random(11.0, 20.0)
    ) : SvgMacro()
}
