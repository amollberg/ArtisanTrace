import kotlinx.serialization.*
import kotlinx.serialization.builtins.list
import kotlinx.serialization.builtins.serializer
import org.openrndr.color.ColorRGBa

@Serializer(forClass = ColorRGBa::class)
object ColorRGBaSerializer : KSerializer<ColorRGBa> {
    override val descriptor: SerialDescriptor =
        PrimitiveDescriptor("ColorRGBa", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ColorRGBa) {
        encoder.encodeSerializableValue(
            Double.serializer().list, listOf(value.r, value.g, value.b, value.a)
        )
    }

    override fun deserialize(decoder: Decoder): ColorRGBa =
        decoder.decodeSerializableValue(
            Double.serializer().list
        ).let { (r, g, b, a) -> ColorRGBa(r, g, b, a) }
}
