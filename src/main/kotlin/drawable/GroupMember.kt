import coordinates.Coordinate

interface GroupMember {
    var groupId: Int

    // The order of this member in the group
    var groupOrdinal: Int

    // TODO: Replace origin with bounds when that is implemented for all
    val origin: Coordinate
    fun draw(drawer: OrientedDrawer)
}
