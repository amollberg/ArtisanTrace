@file:UseSerializers(IntProgressionSerializer::class)
import kotlinx.serialization.*

@Serializable
data class Terminals(val hostInterface: Interface, var range: IntProgression) {
    fun count() = range.count()
}
