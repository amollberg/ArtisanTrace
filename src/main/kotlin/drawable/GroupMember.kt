abstract class GroupMember {
    abstract var groupId: Int
    fun group(groups: Iterable<Group>): Group? =
        groups.firstOrNull {
            it.members.contains(this)
        }

    // The order of this member in the group
    abstract var groupOrdinal: Int

    abstract val bounds: Poly
    abstract fun draw(drawer: OrientedDrawer)
}
