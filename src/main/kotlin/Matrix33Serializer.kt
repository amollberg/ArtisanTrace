import kotlinx.serialization.*
import kotlinx.serialization.builtins.list
import kotlinx.serialization.builtins.serializer
import org.openrndr.math.Matrix33
import org.openrndr.math.Vector3

@Serializer(forClass = Matrix33::class)
object Matrix33Serializer : KSerializer<Matrix33> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveDescriptor("Matrix33", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Matrix33) {
        encoder.encodeSerializableValue(
            Double.serializer().list.list,
            listOf(toList(value[0]), toList(value[1]), toList(value[2]))
        )
    }

    override fun deserialize(decoder: Decoder) =
        decoder.decodeSerializableValue(
            Double.serializer().list.list
        ).let { (c0, c1, c2) ->
            Matrix33.fromColumnVectors(
                toVector3(c0),
                toVector3(c1),
                toVector3(c2)
            )
        }
}

fun toList(v: Vector3) = listOf(v[0], v[1], v[2])

fun toVector3(l: List<Double>): Vector3 {
    assert(l.size == 3)
    return Vector3(l[0], l[1], l[2])
}
