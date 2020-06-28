import coordinates.Coordinate

class GroupAssignTool(viewModel: ViewModel) : BaseTool(viewModel) {
    private val groupMemberSelector = MouseHoverGroupMemberSelector(viewModel)

    // The group to assign to, selected by the first clicked-on group member
    private var selectedGroup: Group? = null

    override fun mouseClicked(position: Coordinate) {
        val groupMember = groupMemberSelector.getGroupMember() ?: return
        val assignedGroup = viewModel.model.groups.firstOrNull {
            it.members.contains(groupMember)
        }
        if (selectedGroup == null) {
            // Select a group
            if (assignedGroup != null) {
                // Select an existing group
                selectedGroup = assignedGroup
            } else {
                // Create a new group and select it
                val newGroup = Group()
                viewModel.model.groups.add(newGroup)
                selectedGroup = newGroup
            }
        }
        // Add the element to the group
        if (!selectedGroup!!.members.contains(groupMember)) {
            assignedGroup?.remove(groupMember)
            selectedGroup!!.add(groupMember)
        }
    }

    override fun draw(drawer: OrientedDrawer) {
        groupMemberSelector.draw(drawer)
    }
}
