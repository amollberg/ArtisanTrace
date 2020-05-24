data class Terminals(val hostInterface: Interface, var range: IntRange) {
    fun count() = range.count()
}
