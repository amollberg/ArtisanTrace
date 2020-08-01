import org.openrndr.color.ColorRGBa

class ComponentSnapSubtool(internal val viewModel: ViewModel) {
    internal val groupMemberSelector = MouseHoverGroupMemberSelector(viewModel)
    private var selectedSnapTarget: GroupMember? = null

    companion object {
        val MARGIN_DISTANCE = 6.0
    }

    fun updateSnapTarget(component: Component, doSnap: Boolean) {
        if (doSnap) {
            if (selectedSnapTarget == null) {
                selectedSnapTarget = groupMemberSelector.getGroupMember(
                    ignore = listOf(component) +
                            component.getConnectedTraces(viewModel.model)
                )
            }
        } else {
            selectedSnapTarget = null
        }
    }

    fun getSnappedPosition(component: Component) =
        selectedSnapTarget?.ifPresent { groupMember ->
            component.system.originCoord + snappedTo(
                component.bounds,
                offsetOutwards(groupMember.bounds, MARGIN_DISTANCE),
                viewModel.mousePoint
            )
        } ?: component.system.originCoord

    fun draw(drawer: OrientedDrawer) {
        val target = selectedSnapTarget ?: return
        isolatedStyle(drawer.drawer, fill = ColorRGBa.BLUE.opacify(0.4)) {
            offsetOutwards(target.bounds, MARGIN_DISTANCE).draw(drawer)
        }
    }
}
