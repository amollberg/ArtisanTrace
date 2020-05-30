import kotlinx.serialization.*
import kotlinx.serialization.builtins.list
import kotlinx.serialization.builtins.serializer

@Serializer(forClass = IntProgression::class)
object IntProgressionSerializer : KSerializer<IntProgression> {
    override val descriptor: SerialDescriptor =
        PrimitiveDescriptor("IntProgression", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: IntProgression) {
        encoder.encodeSerializableValue(
            Int.serializer().list, listOf(value.first, value.last, value.step)
        )
    }

    override fun deserialize(decoder: Decoder) =
        decoder.decodeSerializableValue(
            Int.serializer().list
        ).let { (first, last, step) ->
            IntProgression.fromClosedRange(first, last, step)
        }
}
