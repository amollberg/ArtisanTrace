interface GroupMember {
    var groupId: Int

    // The order of this member in the group
    var groupOrdinal: Int

    fun draw(drawer: OrientedDrawer)
}
