import coordinates.Coordinate
import coordinates.Length
import org.openrndr.KeyModifier.ALT
import org.openrndr.KeyModifier.SHIFT
import org.openrndr.MouseEvent
import org.openrndr.math.Vector2
import kotlin.math.max

class InterfaceMoveTool(viewModel: ViewModel) : BaseTool(viewModel) {
    var selectedItf: Interface? = null
    var mouseOffset = Length(Vector2(0.0, 0.0), viewModel.root)
    internal val interfaceSelector = MouseHoverInterfaceSelector(viewModel)
    internal val groupMemberSelector = MouseHoverGroupMemberSelector(viewModel)
    var hasSelectedItf = false
    var selectedSnapTarget: GroupMember? = null

    override fun mouseClicked(position: Coordinate) {
        if (!hasSelectedItf) {
            // Select the nearest interface
            selectedItf = interfaceSelector.getInterface()
            selectedItf?.ifPresent {
                hasSelectedItf = true
                mouseOffset = viewModel.mousePoint - it.center
            }
        } else {
            // Place the selected interface
            selectedItf?.ifPresent { update(it) }
            hasSelectedItf = false
            selectedItf = null
        }
    }

    override fun mouseScrolled(mouse: MouseEvent) {
        val itf = selectedItf ?: return

        if (mouse.modifiers.contains(SHIFT)) {
            itf.length += 4 * mouse.rotation.y
        } else if (mouse.modifiers.contains(ALT)) {
            var count = itf.terminalCount
            count += mouse.rotation.y.toInt()
            count = max(1, count)

            val highestOccupied =
                (0 until itf.terminalCount).lastOrNull { i ->
                    itf.getConnectedSegments(i, viewModel.model).isNotEmpty()
                } ?: -1
            count = max(highestOccupied + 1, count)
            itf.terminalCount = count
        } else {
            itf.angle -= mouse.rotation.y * 45 % 360
        }
    }

    override fun draw(drawer: OrientedDrawer) {
        interfaceSelector.draw(drawer)

        val itf = selectedItf ?: return
        update(itf)

        itf.draw(drawer)
    }

    internal fun update(itf: Interface) {
        // Interfaces that are in SVG component systems shall not be movable
        // with this tool
        if (itf.center.system == viewModel.root) {
            // Note: A temporary variable is created here because itf.center
            // needs to be untouched while projectOrthogonal is called below
            var newCenter = viewModel.mousePoint - mouseOffset
            // Restrict the new interface position if shift is held
            if (SHIFT in viewModel.modifierKeysHeld) {
                newCenter = projectOrthogonal(newCenter, itf.getTerminals())
            }
            // Snap the interface to a group member bound if alt is held
            if (ALT in viewModel.modifierKeysHeld) {
                if (selectedSnapTarget == null)
                    selectedSnapTarget = groupMemberSelector.getGroupMember(itf)
                selectedSnapTarget?.ifPresent { groupMember ->
                    newCenter = itf.center + snappedTo(
                        itf.bounds,
                        groupMember.bounds,
                        viewModel.mousePoint
                    )
                }
            } else {
                selectedSnapTarget = null
            }
            itf.center = newCenter
        }
    }
}

// Returns the Length to move the movingPoly so that it borders the poly
// while trying to approach the targetPosition
fun snappedTo(movingPoly: Poly, poly: Poly, target: Coordinate):
        Length {
    val originToTarget = target - movingPoly.system!!.originCoord
    val snapSegment = nearestSegment(poly, target)
        ?: return originToTarget
    val snapPoint =
        target.system.coord(
            snapSegment.segment(target.system).project(target.xy()).point
        )
    // Assuming for now that the projection of target is on the snapSegment
    val movingPoint = if (poly.contains(target)) {
        movingPoly.points.minBy { movingPoint ->
            (movingPoint - target).lengthIn(movingPoint.system)
        }
    } else {
        movingPoly.points.maxBy { movingPoint ->
            (movingPoint - target).lengthIn(movingPoint.system)
        }
    } ?: return originToTarget

    return snapPoint - movingPoint
}

fun nearestSegment(poly: Poly, point: Coordinate) =
    poly.segmentPointers.minBy {
        it.segment(point.system).project(point.xy()).distance
    }
