import kotlinx.serialization.*
import kotlinx.serialization.builtins.list
import kotlinx.serialization.builtins.serializer
import org.openrndr.math.Vector2

@Serializer(forClass = Vector2::class)
object Vector2Serializer : KSerializer<Vector2> {
    override val descriptor: SerialDescriptor =
        PrimitiveDescriptor("Vector2", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Vector2) {
        encoder.encodeSerializableValue(
            Double.serializer().list, listOf(value.x, value.y)
        )
    }

    override fun deserialize(decoder: Decoder): Vector2 =
        decoder.decodeSerializableValue(
            Double.serializer().list
        ).let { (x, y) -> Vector2(x, y) }
}
