import org.openrndr.color.ColorRGBa
import org.openrndr.math.Vector2
import org.openrndr.shape.ShapeContour

class MouseHoverGroupMemberSelector(private val viewModel: ViewModel) {

    fun getGroupMember(ignore: List<GroupMember> = emptyList()): GroupMember? =
        viewModel.model.groupMembers.filterNot { it in ignore }.minBy {
            distanceTo(
                it.bounds.contour(viewModel.mousePoint.system),
                viewModel.mousePoint.xy()
            )
        }

    fun draw(drawer: OrientedDrawer) {
        val groupMember = getGroupMember() ?: return
        isolatedStyle(
            drawer.drawer,
            stroke = ColorRGBa.BLUE
        ) {
            groupMember.draw(drawer)
        }
    }
}

fun distanceTo(contour: ShapeContour, point: Vector2): Double {
    if (contour.contains(point)) return 0.0
    return contour.project(point).distance
}
