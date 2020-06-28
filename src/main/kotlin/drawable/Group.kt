import kotlinx.serialization.Serializable

@Serializable
data class Group(
    var interfaces: MutableSet<Interface> = mutableSetOf(),
    var traces: MutableSet<Trace> = mutableSetOf(),
    var sketchComponents: MutableSet<SketchComponent> = mutableSetOf(),
    var svgComponents: MutableSet<SvgComponent> = mutableSetOf(),
    // Only used for serialization
    internal var id: Int = -1
) {
    val members: Set<GroupMember>
        get() = interfaces + traces + sketchComponents + svgComponents

    fun add(groupMember: GroupMember) {
        when (groupMember::class) {
            Interface::class -> interfaces.add(groupMember as Interface)
            Trace::class -> traces.add(groupMember as Trace)
            SketchComponent::class ->
                sketchComponents.add(groupMember as SketchComponent)
            SvgComponent::class ->
                svgComponents.add(groupMember as SvgComponent)
        }
    }

    fun remove(groupMember: GroupMember) {
        when (groupMember::class) {
            Interface::class -> interfaces.remove(groupMember as Interface)
            Trace::class -> traces.remove(groupMember as Trace)
            SketchComponent::class ->
                sketchComponents.remove(groupMember as SketchComponent)
            SvgComponent::class ->
                svgComponents.remove(groupMember as SvgComponent)
        }
    }
}
