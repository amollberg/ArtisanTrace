import kotlinx.serialization.Serializable
import org.openrndr.color.ColorRGBa.Companion.TRANSPARENT
import org.openrndr.color.ColorRGBa.Companion.YELLOW

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

    val surface: Poly
        get() = members.sortedBy { it.groupOrdinal }.map { it.bounds }
            .fold(Poly(emptyList())) { a, b ->
                Poly.fuse(a, b)
            }

    fun add(groupMember: GroupMember) {
        groupMember.groupOrdinal = members.size
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

    fun draw(drawer: OrientedDrawer) {
        interfaces.forEach { it.draw(drawer) }
        traces.forEach { it.draw(drawer) }
        sketchComponents.forEach { it.draw(drawer) }
        svgComponents.forEach { it.draw(drawer) }
        isolatedStyle(
            drawer.drawer,
            stroke = YELLOW,
            fill = TRANSPARENT
        ) {
            if (drawer.extendedVisualization)
                surface.draw(drawer)
        }
    }
}
