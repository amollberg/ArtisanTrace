import coordinates.Coordinate

abstract class GroupMember {
    abstract var groupId: Int
    fun group(groups: Iterable<Group>): Group? =
        groups.firstOrNull {
            it.members.contains(this)
        }

    // The order of this member in the group
    abstract var groupOrdinal: Int

    // TODO: Replace origin with bounds when that is implemented for all
    abstract val origin: Coordinate
    abstract fun draw(drawer: OrientedDrawer)
}
