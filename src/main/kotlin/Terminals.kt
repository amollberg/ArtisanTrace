@file:UseSerializers(IntProgressionSerializer::class)
import kotlinx.serialization.*

@Serializable
data class Terminals(val hostInterface: Interface, var range: IntProgression) {
    fun count() = range.count()

    /** IntRange is subclass of IntProgression but does not compare equal to
     *  an IntProgression with the same elements. So to facilitate
     *  comparison of equal objects as equal, force IntRanges to be
     *  converted to IntProgressions at runtime.
     */
    constructor(hostInterface: Interface, range: IntRange) :
            this(hostInterface, toProgression(range.toList()))
}