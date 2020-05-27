import kotlinx.serialization.*
import org.openrndr.math.Vector2

@Serializer(forClass = Vector2::class)
object Vector2Serializer: KSerializer<Vector2> {
    override val descriptor: SerialDescriptor =
        PrimitiveDescriptor(
            "Vector2",
            PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, obj: Vector2) {
        encoder.encodeString("${obj.x}:${obj.y}")
    }

    override fun deserialize(decoder: Decoder): Vector2 {
        val (x, y) = decoder.decodeString().split(":")
        return Vector2(
            x.toDouble(),
            y.toDouble())
    }
}