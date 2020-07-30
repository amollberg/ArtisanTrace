class InterfaceSnapSubtool(internal val viewModel: ViewModel) {

    internal val groupMemberSelector = MouseHoverGroupMemberSelector(viewModel)
    private var selectedSnapTarget: GroupMember? = null

    companion object {
        val MARGIN_DISTANCE = 6.0
    }

    fun updateSnapTarget(itf: Interface, doSnap: Boolean) {
        if (doSnap) {
            if (selectedSnapTarget == null) {
                selectedSnapTarget = groupMemberSelector.getGroupMember(
                    ignore = listOf(itf) + itf.getConnectedTraces(viewModel.model)
                )
            }
        } else {
            selectedSnapTarget = null
        }
    }

    fun getSnappedPosition(itf: Interface) =
        selectedSnapTarget?.ifPresent { groupMember ->
            itf.center + snappedTo(
                itf.bounds,
                offsetOutwards(groupMember.bounds, MARGIN_DISTANCE),
                viewModel.mousePoint
            )
        } ?: itf.center
}
