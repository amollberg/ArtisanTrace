import kotlinx.serialization.*

@Serializer(forClass = IntProgression::class)
object IntProgressionSerializer : KSerializer<IntProgression> {
    override val descriptor: SerialDescriptor =
        PrimitiveDescriptor("IntProgression", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: IntProgression) {
        encoder.encodeString("${value.first}:${value.last}:${value.step}")
    }

    override fun deserialize(decoder: Decoder): IntProgression {
        val (first, last, step) = decoder.decodeString().split(":")
        return IntProgression.fromClosedRange(
            first.toInt(),
            last.toInt(),
            step.toInt()
        )
    }
}
